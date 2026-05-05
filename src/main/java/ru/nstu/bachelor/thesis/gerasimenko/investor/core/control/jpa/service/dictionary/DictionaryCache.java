package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.service.dictionary;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.exception.InvestorCoreException;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.repo.dictionary.*;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.dictionary.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class DictionaryCache {

    private final InstrumentTypeRepository instrumentTypeRepository;
    private final OperationStateRepository operationStateRepository;
    private final OperationTypeRepository operationTypeRepository;
    private final AccountTypeRepository accountTypeRepository;
    private final AccountStatusRepository accountStatusRepository;
    private final AccountAccessLevelRepository accountAccessLevelRepository;
    private final TariffRepository tariffRepository;

    private final Map<String, InstrumentType> instrumentTypes = new ConcurrentHashMap<>();
    private final Map<String, OperationState> operationStates = new ConcurrentHashMap<>();
    private final Map<String, OperationType> operationTypes = new ConcurrentHashMap<>();
    private final Map<String, AccountType> accountTypes = new ConcurrentHashMap<>();
    private final Map<String, AccountStatus> accountStatuses = new ConcurrentHashMap<>();
    private final Map<String, AccountAccessLevel> accountAccessLevels = new ConcurrentHashMap<>();
    private final Map<String, Tariff> tariffs = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        loadInstrumentTypes();
        loadOperationStates();
        loadOperationTypes();
        loadAccountTypes();
        loadAccountStatuses();
        loadAccountAccessLevels();
        loadTariffs();
        log.info("DictionaryCache initialized: instrumentTypes={}, operationStates={}, operationTypes={}, accountTypes={}, accountStatuses={}, accountAccessLevels={}, tariffs={}",
                instrumentTypes.size(), operationStates.size(), operationTypes.size(),
                accountTypes.size(), accountStatuses.size(), accountAccessLevels.size(), tariffs.size());
    }

    private void loadInstrumentTypes() {
        instrumentTypeRepository.findAll().forEach(t -> instrumentTypes.put(t.getType(), t));
    }

    private void loadOperationStates() {
        operationStateRepository.findAll().forEach(s -> operationStates.put(s.getState(), s));
    }

    private void loadOperationTypes() {
        operationTypeRepository.findAll().forEach(t -> operationTypes.put(t.getType(), t));
    }

    private void loadAccountTypes() {
        accountTypeRepository.findAll().forEach(t -> accountTypes.put(t.getType(), t));
    }

    private void loadAccountStatuses() {
        accountStatusRepository.findAll().forEach(s -> accountStatuses.put(s.getStatus(), s));
    }

    private void loadAccountAccessLevels() {
        accountAccessLevelRepository.findAll().forEach(l -> accountAccessLevels.put(l.getLevel(), l));
    }

    private void loadTariffs() {
        tariffRepository.findAll().forEach(t -> tariffs.put(t.getTariff(), t));
    }

    public InstrumentType getInstrumentType(String type) {
        InstrumentType value = instrumentTypes.get(type);

        if (value == null) {
            log.warn("InstrumentType not found: {}", type);
            refresh();
            value = instrumentTypes.get(type);

            if (value == null) {
                log.error("InstrumentType not found after refreshing cache: {}", type);
                throw new InvestorCoreException("InstrumentType not found: " + type);
            }
        }
        return value;
    }

    public OperationState getOperationState(String state) {
        OperationState value = operationStates.get(state);

        if (value == null) {
            log.warn("OperationState not found: {}", state);
            refresh();
            value = operationStates.get(state);

            if (value == null) {
                log.error("OperationState not found after refreshing cache: {}", state);
                throw new InvestorCoreException("OperationState not found: " + state);
            }
        }
        return value;
    }

    public OperationType getOperationType(String type) {
        OperationType value = operationTypes.get(type);

        if (value == null) {
            log.warn("OperationType not found: {}", type);
            refresh();
            value = operationTypes.get(type);

            if (value == null) {
                log.error("OperationType not found after refreshing cache: {}", type);
                throw new InvestorCoreException("OperationType not found: " + type);
            }
        }
        return value;
    }

    public AccountType getAccountType(String type) {
        AccountType value = accountTypes.get(type);

        if (value == null) {
            log.warn("AccountType not found: {}", type);
            refresh();
            value = accountTypes.get(type);

            if (value == null) {
                log.error("AccountType not found after refreshing cache: {}", type);
                throw new InvestorCoreException("AccountType not found: " + type);
            }
        }
        return value;
    }

    public AccountStatus getAccountStatus(String status) {
        AccountStatus value = accountStatuses.get(status);

        if (value == null) {
            log.warn("AccountStatus not found: {}", status);
            refresh();
            value = accountStatuses.get(status);

            if (value == null) {
                log.error("AccountStatus not found after refreshing cache: {}", status);
                throw new InvestorCoreException("AccountStatus not found: " + status);
            }
        }
        return value;
    }

    public AccountAccessLevel getAccountAccessLevel(String level) {
        AccountAccessLevel value = accountAccessLevels.get(level);
        if (value == null) {
            log.warn("AccountAccessLevel not found: {}", level);
            refresh();
            value = accountAccessLevels.get(level);

            if (value == null) {
                log.error("AccountAccessLevel not found after refreshing cache: {}", level);
                throw new InvestorCoreException("AccountAccessLevel not found: " + level);
            }
        }
        return value;
    }

    public Tariff getTariff(String tariff) {
        Tariff value = tariffs.get(tariff);
        if (value == null) {
            log.warn("Tariff not found: {}", tariff);
            refresh();
            value = tariffs.get(tariff);

            if (value == null) {
                log.error("Tariff not found after refreshing cache: {}", tariff);
                throw new InvestorCoreException("Tariff not found: " + tariff);
            }
        }
        return value;
    }

    public void refresh() {
        log.info("Refreshing DictionaryCache...");
        instrumentTypes.clear();
        operationStates.clear();
        operationTypes.clear();
        accountTypes.clear();
        accountStatuses.clear();
        accountAccessLevels.clear();
        tariffs.clear();
        init();
    }
}