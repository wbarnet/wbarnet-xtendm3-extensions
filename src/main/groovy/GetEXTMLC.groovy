/**
 * Name:    EXT003MI_GetEXTMLC
 * Description:
 *          API to fetch data from EXTMLC table
 *
 * Usage:
 *  Arguments
 * @CONO  Numeric,3       Company
 * @WHLO  Alphanumeric,3  Warehouse
 * @ITNO  Alphanumeric,15 Item Number
 * @WHSL  Alphanumeric,10 Location
 * @BANO  Alphanumeric,20 Lot number
 * @CAMU  Alphanumeric,20 Container 
 * @NOTB  Numeric,10      No. of Tubes
 * @SPKG  Numeric,18      Supplier Package
 *
 * Created By: Birlasoft Ltd.
 * Authors:    Priyanka Nadgouda  
 *
 * History:
 *20220201  base script created
 *
 */
public class GetEXTMLC extends ExtendM3Transaction {
  private final MIAPI mi;
  private final DatabaseAPI database;

  public GetEXTMLC(MIAPI mi, DatabaseAPI database) {
    this.mi = mi;
    this.database = database;
  }

  //Input variables
  private int iCONO;
  private String iWHLO;
  private String iITNO;
  private String iWHSL;
  private String iBANO;
  private String iCAMU;
  private int iREPN;
  private int iNOTB;
  private String iSPKG;
  private boolean hasError = false;


  /**
   * Main method to fetch data from GetEXTMLC
   **/
  public void main() {

    iCONO = mi.in.CONO;
    iWHLO = mi.in.WHLO;
    iITNO = mi.in.ITNO;
    iWHSL = mi.in.WHSL;
    iBANO = mi.in.BANO;
    iCAMU = mi.in.CAMU;
    iREPN = mi.in.REPN;


    DBAction query = database.table("EXTMLC").index("00").selectAllFields().build();
    DBContainer container = query.getContainer();
    container.set("EXCONO",iCONO);
    container.set("EXWHLO",iWHLO);
    container.set("EXITNO",iITNO);
    container.set("EXWHSL",iWHSL);
    container.set("EXBANO",iBANO);
    container.set("EXCAMU",iCAMU);
    container.set("EXREPN",iREPN);


    /**
     * Check if record exists in EXTMLC
     **/
    if(!query.read(container)){
      mi.error("Record does not exist in custom table");
    }
    else{

      mi.outData.put("CONO",container.get("EXCONO").toString());
      mi.outData.put("WHLO",container.get("EXWHLO").toString());
      mi.outData.put("ITNO",container.get("EXWHSL").toString());
      mi.outData.put("WHSL",container.get("EXWHSL").toString());
      mi.outData.put("BANO",container.get("EXBANO").toString());
      mi.outData.put("CAMU",container.get("EXREPN").toString());
      mi.outData.put("REPN",container.get("EXCONO").toString());
      mi.outData.put("NOTB",container.get("EXNOTB").toString());
      mi.outData.put("SPKG",container.get("EXSPKG").toString());


      mi.write();
    }
  }
}
