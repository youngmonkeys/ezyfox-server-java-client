package com.tvd12.ezyfoxserver.client.testing.ssl;

import com.tvd12.ezyfoxserver.client.ssl.EzySslTrustManagerFactory;
import org.testng.annotations.Test;

import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.lang.reflect.Method;
import java.security.KeyStore;

import static org.mockito.Mockito.mock;

public class EzySslTrustManagerFactoryTest {

    @Test
    public void test() throws Exception {
        EzySslTrustManagerFactory factory = EzySslTrustManagerFactory
            .getInstance();
        Method engineInit1 = EzySslTrustManagerFactory.class
            .getDeclaredMethod("engineInit", ManagerFactoryParameters.class);
        engineInit1.setAccessible(true);
        ManagerFactoryParameters managerFactoryParameters =
            mock(ManagerFactoryParameters.class);
        engineInit1.invoke(factory, managerFactoryParameters);

        Method engineInit2 = EzySslTrustManagerFactory.class
            .getDeclaredMethod("engineInit", KeyStore.class);
        engineInit2.setAccessible(true);
        KeyStore keyStore = KeyStore.getInstance("JKS");
        engineInit2.invoke(factory, keyStore);

        Method engineGetTrustManagers = EzySslTrustManagerFactory.class
            .getDeclaredMethod("engineGetTrustManagers");
        engineGetTrustManagers.setAccessible(true);
        TrustManager[] trustManagers = (TrustManager[]) engineGetTrustManagers.invoke(factory);
        X509TrustManager trustManagerAt0 = (X509TrustManager) trustManagers[0];
        trustManagerAt0.getAcceptedIssuers();
        trustManagerAt0.checkClientTrusted(null, null);
        trustManagerAt0.checkServerTrusted(null, null);
    }


}
