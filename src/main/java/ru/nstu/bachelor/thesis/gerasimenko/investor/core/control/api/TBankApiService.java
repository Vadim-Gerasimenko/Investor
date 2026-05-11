package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.config.InvestorCoreConfig;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.converter.MoneyValueConverter;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.converter.TBankInstrumentConverter;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.converter.TBankTradeConverter;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.exception.AccountNotFoundException;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.exception.InvestorCoreException;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.http.HttpInteractionService;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.service.dictionary.DictionaryCache;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.service.auth.TBankTokenService;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.service.auth.UserService;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.service.tbank.*;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.router.Router;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.utils.HttpUtils;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.network.HttpHeaders;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.tbank.*;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.enums.tbank.TBankInstrumentsInfo;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.auth.User;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.dictionary.InstrumentType;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.dictionary.Tariff;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.tbank.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.constant.PreciousMetals.PRECIOUS_METALS_FIGI;
import static ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.enums.tbank.MainApiMethod.*;
import static ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.enums.tbank.account.AccountAccessLevel.ACCOUNT_ACCESS_LEVEL_FULL_ACCESS;
import static ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.enums.tbank.account.AccountAccessLevel.ACCOUNT_ACCESS_LEVEL_READ_ONLY;
import static ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.enums.tbank.account.AccountStatus.ACCOUNT_STATUS_CLOSED;
import static ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.enums.tbank.account.AccountStatus.ACCOUNT_STATUS_OPEN;
import static ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.enums.tbank.account.AccountType.*;
import static ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.enums.tbank.operation.InstrumentType.PRECIOUS_METAL;

@Slf4j
@Service
@RequiredArgsConstructor
public class TBankApiService {

    private final InvestorCoreConfig config;
    private final HttpInteractionService httpService;

    private final TBankTokenService tBankTokenService;
    private final TBankAccountService tBankAccountService;

    private final TBankInstrumentService tBankInstrumentService;
    private final TBankInstrumentPriceService tBankInstrumentPriceService;
    private final TBankOperationService tBankOperationService;
    private final TBankTradeService tBankTradeService;
    private final TBankUserTariffService tBankUserTariffService;

    private final Router router;

    private final DictionaryCache dictionaryCache;
    private final UserService userService;

    @Transactional
    public void syncTBankAccountData(@AuthenticationPrincipal User user) {
        log.info("to synchronization t-bank user data: userId=[{}]", user.getId());

        TBankAccount account = null;
        try {
            account = tBankAccountService.findActiveAccount(user);
        } catch (AccountNotFoundException ignored) {
        }

        if (account == null || account.getLastSyncAt() == null || account.getLastSyncAt().isBefore(
                LocalDateTime.now().minus(config.getSynchronization().getAccountSyncMaxDelay()))) {
            updateUserTariff(user);
            updateAccounts(user);
            updateOperations(user);
        } else {
            log.info("T-Bank user was synchronized earlier: userId=[{}], lastSyncTime=[{}]",
                    user.getId(), account.getLastSyncAt());
        }

        log.info("from synchronization t-bank user data: userId=[{}]", user.getId());
    }

    @Transactional
    public TBankOperationsDto updateOperations(@AuthenticationPrincipal User user) {
        TBankAccount account = tBankAccountService.findActiveAccount(user);
        return updateOperations(user, account);
    }

    @Transactional
    public TBankOperationsDto updateOperations(@AuthenticationPrincipal User user, String accountName) {
        TBankAccount account = tBankAccountService.findAccountByName(user, accountName);
        return updateOperations(user, account);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TBankOperationsDto updateOperations(@AuthenticationPrincipal User user, TBankAccount account)
            throws ResponseStatusException {
        log.info("to update operations: userId=[{}], accountName=[{}]", user.getId(), account.getAccountName());

        log.info("Received request: service=[{}], method=[{}]", config.getTBankApi().getOperationsServiceName(), GET_OPERATIONS.getMethod());
        String url = HttpUtils.getUrl(router.getRouteToOperations(), GET_OPERATIONS.getMethod());
        log.info("Generated url to route: {}", url);

        HttpHeaders headers = HttpHeaders.restHeaders().bearerToken(tBankTokenService.getActiveToken(user).getToken());

        OperationsRequestDto requestBody = OperationsRequestDto.builder()
                .accountId(account.getId())
                .build();
        ResponseEntity<TBankOperationsDto> response = httpService.post(url, headers, requestBody, TBankOperationsDto.class);

        if (!response.getStatusCode().is2xxSuccessful()
                || response.getBody() == null
                || response.getBody().operations() == null) {
            throw new ResponseStatusException(response.getStatusCode()); //TODO
        }

        for (TBankOperationDto operationDto : response.getBody().operations()) {
            TBankOperation operation = TBankOperation.builder()
                    .id(operationDto.id())
                    .parentOperation(tBankOperationService.findById(operationDto.parentOperationId()))
                    .account(account)
                    .operationType(dictionaryCache.getOperationType(operationDto.operationType().getType()))
                    .state(dictionaryCache.getOperationState(operationDto.state().getState()))
                    .currency(operationDto.currency().toUpperCase())
                    .paymentValue(MoneyValueConverter.convert(operationDto.payment()))
                    .quantity(operationDto.quantity())
                    .quantityRest(operationDto.quantityRest())
                    .instrument(tBankInstrumentService.findById(operationDto.instrumentUid()).orElse(null))
                    .operationDate(operationDto.date())
                    .build();
            tBankOperationService.save(operation);

            List<TBankTrade> trades = operationDto.trades().stream()
                    .map(tradeDto -> TBankTradeConverter.convert(tradeDto, operation))
                    .toList();

            if (!trades.isEmpty()) {
                tBankTradeService.saveAll(trades);
                operation.setTrades(trades);
                tBankOperationService.save(operation);
            }
        }

        tBankAccountService.updateLastSyncTime(account);
        log.info("from update operations: userId=[{}], accountName=[{}]", user.getId(), account.getAccountName());
        return response.getBody();
    }

    @Transactional
    public void updateUserTariff(@AuthenticationPrincipal User user) {
        log.info("to update user tariff: userId=[{}]", user.getId());

        log.info("Received request: service=[{}], method=[{}]", config.getTBankApi().getUsersServiceName(), GET_INFO.getMethod());
        String url = HttpUtils.getUrl(router.getRouteToUsers(), GET_INFO.getMethod());
        log.info("Generated url to route: {}", url);

        HttpHeaders headers = HttpHeaders.restHeaders().bearerToken(tBankTokenService.getActiveToken(user).getToken());
        BaseRequestDto requestBody = BaseRequestDto.builder().build();

        ResponseEntity<TBankTariffDto> response = httpService.post(url, headers, requestBody, TBankTariffDto.class);

        if (!response.getStatusCode().is2xxSuccessful()
                || response.getBody() == null
                || response.getBody().tariff() == null
                || response.getBody().tariff().isEmpty()) {
            throw new ResponseStatusException(response.getStatusCode()); //TODO
        }

        Tariff tariff = dictionaryCache.getTariff(response.getBody().tariff());
        tBankUserTariffService.updateUserTariff(user, tariff);

        log.info("from update user tariff: userId=[{}], tariff=[{}]", user.getId(), tariff.getTariff());
    }

    @Transactional
    public void updateAccounts(@AuthenticationPrincipal User user) throws ResponseStatusException {
        log.info("to update accounts: userId=[{}]", user.getId());

        log.info("Received request: service=[{}], method=[{}]", config.getTBankApi().getUsersServiceName(), GET_ACCOUNTS.getMethod());
        String url = HttpUtils.getUrl(router.getRouteToUsers(), GET_ACCOUNTS.getMethod());
        log.info("Generated url to route: {}", url);

        HttpHeaders headers = HttpHeaders.restHeaders().bearerToken(tBankTokenService.getActiveToken(user).getToken());
        BaseRequestDto requestBody = BaseRequestDto.builder().build();
        ResponseEntity<TBankAccountsDto> response = httpService.post(url, headers, requestBody, TBankAccountsDto.class);

        if (!response.getStatusCode().is2xxSuccessful()
                || response.getBody() == null
                || response.getBody().accounts() == null
                || response.getBody().accounts().isEmpty()) {
            throw new ResponseStatusException(response.getStatusCode()); //TODO
        }

        TBankAccountsDto accountsDto = response.getBody();
        String activeAccountId = tBankAccountService.findActiveAccountIdOrElseNull(user);

        List<TBankAccount> accounts = accountsDto.accounts().stream()
                .filter(acc -> ((ACCOUNT_TYPE_TINKOFF.equals(acc.type())
                        || ACCOUNT_TYPE_TINKOFF_IIS.equals(acc.type())
                        || ACCOUNT_TYPE_INVEST_BOX.equals(acc.type())
                        || ACCOUNT_TYPE_INVEST_FUND.equals(acc.type()))
                        && (ACCOUNT_STATUS_OPEN.equals(acc.status())
                        || ACCOUNT_STATUS_CLOSED.equals(acc.status()))
                        && (ACCOUNT_ACCESS_LEVEL_READ_ONLY.equals(acc.accessLevel())
                        || ACCOUNT_ACCESS_LEVEL_FULL_ACCESS.equals(acc.accessLevel()))
                )).map(acc ->
                        TBankAccount.builder()
                                .id(acc.id())
                                .accountName(acc.name())
                                .user(user)
                                .type(dictionaryCache.getAccountType(acc.type().getType()))
                                .status(dictionaryCache.getAccountStatus(acc.status().getStatus()))
                                .accessLevel(dictionaryCache.getAccountAccessLevel(acc.accessLevel().getLevel()))
                                .openedDate(acc.openedDate())
                                .closedDate(acc.closedDate())
                                .build()
                )
                .toList();

        TBankAccount mainAccount = accounts.stream()
                .filter(acc -> activeAccountId != null && activeAccountId.equals(acc.getId()))
                .findFirst()
                .orElse(accounts.stream()
                        .filter(acc -> (activeAccountId != null && activeAccountId.equals(acc.getId()))
                                || (ACCOUNT_TYPE_TINKOFF.getType().equals(acc.getType().getType())
                                && ACCOUNT_STATUS_OPEN.getStatus().equals(acc.getStatus().getStatus())))
                        .max(Comparator.comparing(TBankAccount::getOpenedDate))
                        .orElse(accounts.stream()
                                .findFirst()
                                .orElseThrow(() -> new InvestorCoreException("No accounts found"))));

        tBankAccountService.saveAll(accounts);
        tBankAccountService.activateAccount(user, mainAccount.getAccountName());
        log.info("from update accounts: userId=[{}], activeAccountName=[{}], accountsNames=[{}]",
                user.getId(),
                mainAccount.getAccountName(),
                accounts.stream()
                        .map(TBankAccount::getAccountName)
                        .collect(Collectors.joining(",")));
    }

    @Transactional
    public void updateInstruments(@AuthenticationPrincipal User user) {
        Arrays.stream(TBankInstrumentsInfo.values())
                .forEach(instrument -> updateInstruments(user, instrument));
    }

    @Transactional
    public void updateInstruments() {
        updateInstruments(findSystemUser());
    }

    @Transactional
    public void updateInstrumentsPrices() {
        updateInstrumentsPrices(findSystemUser());
    }

    private User findSystemUser() {
        return userService.findByEmail(config.getSystemUserEmail());
    }

    @Transactional
    public void updateInstrumentsPrices(@AuthenticationPrincipal User user) {
        List<String> uids = tBankInstrumentService.findAllUids();

        int instrumentsCount = uids.size();
        final int maxPartitionSize = 3000;

        log.info("to update instruments prices: instrumentsCount=[{}], partitionsCount=[{}]",
                instrumentsCount, (instrumentsCount + maxPartitionSize - 1) / maxPartitionSize);

        log.info("Received request: service=[{}], method=[{}]", config.getTBankApi().getMarketDataServiceName(), GET_MARKET_VALUES.getMethod());
        String url = HttpUtils.getUrl(router.getRouteToMarketData(), GET_MARKET_VALUES.getMethod());
        log.info("Generated url to route: {}", url);

        for (int i = 0, partitionNumber = 1; i < instrumentsCount; i += 3000, ++partitionNumber) {
            log.info("trying to update partition number #{}", partitionNumber);

            List<TBankInstrumentPrice> instrumentsPrices = new LinkedList<>();
            List<String> uidSubList = uids.subList(i, Math.min(i + 3000, instrumentsCount));

            HttpHeaders headers = HttpHeaders.restHeaders().bearerToken(tBankTokenService.getActiveToken(user).getToken());
            InstrumentsValuesRequestDto requestBody = new InstrumentsValuesRequestDto(uidSubList);

            ResponseEntity<InstrumentsValuesResponseDto> response =
                    httpService.post(url, headers, requestBody, InstrumentsValuesResponseDto.class);

            if (!response.getStatusCode().is2xxSuccessful()
                    || response.getBody() == null
                    || response.getBody().instruments() == null
                    || response.getBody().instruments().isEmpty()) {
                throw new ResponseStatusException(response.getStatusCode()); //TODO
            }

            response.getBody().instruments().forEach(dto -> {
                try {
                    final String brokenInstrumentTicker = "SU26242RMFS6";/*List.of(
                            "dad5316e-70f6-4fb0-aca3-bcd085520a7b",
                            "a93c7ae6-62b7-4824-adf6-f18905d28e30",
                            "aeb87814-40bc-4d36-bf59-9242e3503a04");*/

                    long priceNano = MoneyValueConverter.convert(dto.values().getFirst().value());
                    if (brokenInstrumentTicker.equals(dto.ticker())) {
                        // лютейший костыль :) API некорректно учитывает лотность, хотя в stream-ах выдает верные результаты
                        // но я использую REST :((
                        priceNano = priceNano * 10;
                    }

                    instrumentsPrices.add(TBankInstrumentPrice.builder()
                            .instrumentUid(dto.instrumentUid())
                            .price(priceNano)
                            .recordedAt(dto.values().getFirst().time())
                            .build());
                } catch (NoSuchElementException ignored) {
                }
            });

            tBankInstrumentPriceService.saveAll(instrumentsPrices);
            log.info("partition number #{} updated successfully", partitionNumber);
        }

        log.info("from update instruments prices: updatedCount=[{}]", instrumentsCount);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateInstruments(@AuthenticationPrincipal User user, TBankInstrumentsInfo instrumentsInfo)
            throws ResponseStatusException {
        log.info("to update instruments ({})", instrumentsInfo.getOfficialName());

        log.info("Received request: service=[{}], method=[{}]", config.getTBankApi().getInstrumentsServiceName(), instrumentsInfo.getOfficialName());
        String url = HttpUtils.getUrl(router.getRouteToInstruments(), instrumentsInfo.getOfficialName());
        log.info("Generated url to route: {}", url);

        HttpHeaders headers = HttpHeaders.restHeaders().bearerToken(tBankTokenService.getActiveToken(user).getToken());
        InstrumentsRequestDto requestBody = InstrumentsRequestDto.byDefault();
        ResponseEntity<TBankInstrumentsDto> response = httpService.post(url, headers, requestBody, TBankInstrumentsDto.class);

        if (!response.getStatusCode().is2xxSuccessful()
                || response.getBody() == null
                || response.getBody().instruments() == null
                || response.getBody().instruments().isEmpty()) {
            throw new ResponseStatusException(response.getStatusCode()); //TODO
        }

        List<TBankInstrument> instruments = new LinkedList<>();
        List<InstrumentDto> instrumentDtoList = response.getBody().instruments();

        for (InstrumentDto dto : instrumentDtoList) {
            TBankInstrument existing = tBankInstrumentService.findById(dto.uid())
                    .orElseGet(() -> {
                        List<TBankInstrument> instrumentList = tBankInstrumentService.findByFigi(dto.figi());
                        return !instrumentList.isEmpty() ? instrumentList.getFirst() : null;
                    });

            InstrumentType instrumentType = dictionaryCache.getInstrumentType(instrumentsInfo.getType().getType());

            if (instrumentsInfo.equals(TBankInstrumentsInfo.CURRENCIES) && PRECIOUS_METALS_FIGI.contains(dto.figi())) {
                instrumentType = dictionaryCache.getInstrumentType(PRECIOUS_METAL.getType());
            }

            if (existing != null) {
                existing.setName(dto.name());
                existing.setFigi(dto.figi());
                existing.setTicker(dto.ticker());
                existing.setIsin(dto.isin());
                existing.setLot(dto.lot());
                existing.setCurrency(dto.currency().toUpperCase());
                existing.setInstrumentType(instrumentType);
                instruments.add(existing);
            } else {
                instruments.add(TBankInstrumentConverter.convert(dto, instrumentType));
            }
        }

        tBankInstrumentService.saveAll(instruments);
        log.info("from update instruments ({}): updatedCount=[{}]", instrumentsInfo.getOfficialName(), instruments.size());
    }
}