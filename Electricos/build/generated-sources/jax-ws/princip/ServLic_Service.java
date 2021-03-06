
package princip;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.6-1b01 
 * Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "servLic", targetNamespace = "http://princip/", wsdlLocation = "http://localhost:8080/servLic/servLic?wsdl")
public class ServLic_Service
    extends Service
{

    private final static URL SERVLIC_WSDL_LOCATION;
    private final static WebServiceException SERVLIC_EXCEPTION;
    private final static QName SERVLIC_QNAME = new QName("http://princip/", "servLic");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("http://localhost:8080/servLic/servLic?wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        SERVLIC_WSDL_LOCATION = url;
        SERVLIC_EXCEPTION = e;
    }

    public ServLic_Service() {
        super(__getWsdlLocation(), SERVLIC_QNAME);
    }

    public ServLic_Service(WebServiceFeature... features) {
        super(__getWsdlLocation(), SERVLIC_QNAME, features);
    }

    public ServLic_Service(URL wsdlLocation) {
        super(wsdlLocation, SERVLIC_QNAME);
    }

    public ServLic_Service(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, SERVLIC_QNAME, features);
    }

    public ServLic_Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public ServLic_Service(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns ServLic
     */
    @WebEndpoint(name = "servLicPort")
    public ServLic getServLicPort() {
        return super.getPort(new QName("http://princip/", "servLicPort"), ServLic.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns ServLic
     */
    @WebEndpoint(name = "servLicPort")
    public ServLic getServLicPort(WebServiceFeature... features) {
        return super.getPort(new QName("http://princip/", "servLicPort"), ServLic.class, features);
    }

    private static URL __getWsdlLocation() {
        if (SERVLIC_EXCEPTION!= null) {
            throw SERVLIC_EXCEPTION;
        }
        return SERVLIC_WSDL_LOCATION;
    }

}
