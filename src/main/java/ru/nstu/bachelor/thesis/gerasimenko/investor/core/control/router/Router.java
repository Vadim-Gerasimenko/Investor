package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.router;

import org.springframework.stereotype.Component;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.config.InvestorCoreConfig;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.config.TBankApiConfig;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.utils.HttpUtils;

@Component
public class Router {

    private final TBankApiConfig bankApiConfig;
    private final String baseUrlWithContract;

    public Router(InvestorCoreConfig investorCoreConfig) {
        this.bankApiConfig = investorCoreConfig.getTBankApi();
        this.baseUrlWithContract = HttpUtils.getUrl(bankApiConfig.getBaseUrl(), bankApiConfig.getContractVersion());
    }

    public String getRouteToOperations() {
        return getRouteToService(baseUrlWithContract, bankApiConfig.getOperationsServiceName());
    }

    public String getRouteToInstruments() {
        return getRouteToService(baseUrlWithContract, bankApiConfig.getInstrumentsServiceName());
    }

    public String getRouteToMarketData() {
        return getRouteToService(baseUrlWithContract, bankApiConfig.getMarketDataServiceName());
    }

    public String getRouteToUsers() {
        return getRouteToService(baseUrlWithContract, bankApiConfig.getUsersServiceName());
    }

    public String getRouteToSandbox() {
        return getRouteToService(baseUrlWithContract, bankApiConfig.getSandboxServiceName());
    }

    private static String getRouteToService(String baseUrlWithContract, String serviceName) {
        return HttpUtils.getUrlWithService(baseUrlWithContract, serviceName);
    }
}