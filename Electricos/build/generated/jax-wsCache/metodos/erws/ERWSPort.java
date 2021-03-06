
package erws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.6-1b01 
 * Generated source version: 2.2
 * 
 */
@WebService(name = "ERWSPort", targetNamespace = "urn:ERWS")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface ERWSPort {


    /**
     * 
     * @return
     *     returns java.lang.String
     */
    @WebMethod(action = "urn:ERWS#sPrueCon")
    @WebResult(name = "out", partName = "out")
    public String sPrueCon();

    /**
     * 
     * @param operando1
     * @param operando2
     * @return
     *     returns java.lang.String
     */
    @WebMethod(action = "urn:ERWS#sumar2")
    @WebResult(name = "respuesta", partName = "respuesta")
    public String sumar2(
        @WebParam(name = "operando1", partName = "operando1")
        float operando1,
        @WebParam(name = "operando2", partName = "operando2")
        float operando2);

    /**
     * 
     * @param sMacP
     * @return
     *     returns java.lang.String
     */
    @WebMethod(action = "urn:ERWS#vGivMac")
    @WebResult(name = "respuesta", partName = "respuesta")
    public String vGivMac(
        @WebParam(name = "sMacP", partName = "sMacP")
        String sMacP);

    /**
     * 
     * @param sKeyU
     * @param sSerU
     * @param sMac
     * @return
     *     returns java.lang.String
     */
    @WebMethod(action = "urn:ERWS#vRevoSis")
    @WebResult(name = "respuesta", partName = "respuesta")
    public String vRevoSis(
        @WebParam(name = "sSerU", partName = "sSerU")
        String sSerU,
        @WebParam(name = "sKeyU", partName = "sKeyU")
        String sKeyU,
        @WebParam(name = "sMac", partName = "sMac")
        String sMac);

    /**
     * 
     * @param sKeyU
     * @param sSerU
     * @param sMac
     * @return
     *     returns java.lang.String
     */
    @WebMethod(action = "urn:ERWS#vSerKeyU")
    @WebResult(name = "respuesta", partName = "respuesta")
    public String vSerKeyU(
        @WebParam(name = "sSerU", partName = "sSerU")
        String sSerU,
        @WebParam(name = "sKeyU", partName = "sKeyU")
        String sKeyU,
        @WebParam(name = "sMac", partName = "sMac")
        String sMac);

    /**
     * 
     * @param sMac
     * @return
     *     returns java.lang.String
     */
    @WebMethod(action = "urn:ERWS#vAvisNot")
    @WebResult(name = "respuesta", partName = "respuesta")
    public String vAvisNot(
        @WebParam(name = "sMac", partName = "sMac")
        String sMac);

    /**
     * 
     * @param sMac
     * @return
     *     returns java.lang.String
     */
    @WebMethod(action = "urn:ERWS#vMsjMac")
    @WebResult(name = "respuesta", partName = "respuesta")
    public String vMsjMac(
        @WebParam(name = "sMac", partName = "sMac")
        String sMac);

    /**
     * 
     * @param sMsj
     * @param sSer
     * @param sMac
     * @param sKey
     * @return
     *     returns java.lang.String
     */
    @WebMethod(action = "urn:ERWS#sRegMsj")
    @WebResult(name = "respuesta", partName = "respuesta")
    public String sRegMsj(
        @WebParam(name = "sMac", partName = "sMac")
        String sMac,
        @WebParam(name = "sMsj", partName = "sMsj")
        String sMsj,
        @WebParam(name = "sSer", partName = "sSer")
        String sSer,
        @WebParam(name = "sKey", partName = "sKey")
        String sKey);

    /**
     * 
     * @param ser
     * @return
     *     returns java.lang.String
     */
    @WebMethod(action = "urn:ERWS#consultaSer")
    @WebResult(name = "respuesta", partName = "respuesta")
    public String consultaSer(
        @WebParam(name = "ser", partName = "ser")
        String ser);

    /**
     * 
     * @param sKeyU
     * @param sCP
     * @param sCel
     * @param sSerU
     * @param sPers
     * @param sRazSoc
     * @param sColonia
     * @param sTel
     * @param sCalle
     * @param sNi
     * @return
     *     returns java.lang.String
     */
    @WebMethod(action = "urn:ERWS#asignaKey")
    @WebResult(name = "ser", partName = "ser")
    public String asignaKey(
        @WebParam(name = "sSerU", partName = "sSerU")
        String sSerU,
        @WebParam(name = "sKeyU", partName = "sKeyU")
        String sKeyU,
        @WebParam(name = "sRazSoc", partName = "sRazSoc")
        String sRazSoc,
        @WebParam(name = "sCalle", partName = "sCalle")
        String sCalle,
        @WebParam(name = "sColonia", partName = "sColonia")
        String sColonia,
        @WebParam(name = "sCP", partName = "sCP")
        String sCP,
        @WebParam(name = "sNi", partName = "sNi")
        String sNi,
        @WebParam(name = "sTel", partName = "sTel")
        String sTel,
        @WebParam(name = "sCel", partName = "sCel")
        String sCel,
        @WebParam(name = "sPers", partName = "sPers")
        String sPers);

}
