/**
 * Name:    EXT004MI_GetEXTTRA
 * Description:
 *          API to get data from EXTTRA table
 *
 * Usage:
 *  Arguments
 * @CONO  Numric,3        Company
 * @WHLO  Alphanumeric,3  Warehouse
 * @ITNO  Alphanumeric,15 Item Number
 * @RGDT  Numeric,8       Entry Date
 * @RGTM  Numeric,6       Entry time
 * @TMSX  Numeric,3       Time suffix
 * @NOTB  Numeric,18      No. of Tubes
 * @SPKG  Alphanumeric,30 Supplier Package
 *
 * Created By: Birlasoft Ltd.
 * Authors:    Aditya Bhatkhande
 *
 * History:
 *  20220201  base script created
 *
 */

public class GetEXTTRA extends ExtendM3Transaction {
  private final MIAPI mi;
  private final DatabaseAPI database;

  private int iCONO;
  private String iWHLO;
  private String iITNO;
  private int iRGDT;
  private int iRGTM;
  private int iTMSX;
  private double iNOTB;
  private String iSPKG;

  public GetEXTTRA(MIAPI mi, DatabaseAPI database) {
    this.mi = mi;
    this.database = database;
  }

  public void main() {

    iCONO = mi.in.CONO;
    iWHLO = mi.in.WHLO;
    iITNO = mi.in.ITNO;
    iRGDT = mi.in.RGDT;
    iRGTM = mi.in.RGTM;
    iTMSX = mi.in.TMSX;
    iNOTB = (Double)(mi.in.NOTB == null?0:mi.in.NOTB);
    iSPKG = mi.in.SPKG;

    DBAction query = database.table("EXTTRA").index("00").selectAllFields().build();
    DBContainer container = query.getContainer();
    container.set("EXCONO",iCONO);
    container.set("EXWHLO",iWHLO);
    container.set("EXITNO",iITNO);
    container.set("EXRGDT",iRGDT);
    container.set("EXRGTM",iRGTM);
    container.set("EXTMSX",iTMSX);


    // Check if record exists
    if(!query.read(container)){
      mi.error("Record does not exist in custom table");
    }
    else{

      mi.outData.put("CONO",container.get("EXCONO").toString());
      mi.outData.put("WHLO",container.get("EXWHLO").toString());
      mi.outData.put("ITNO",container.get("EXITNO").toString());
      mi.outData.put("RGDT",container.get("EXRGDT").toString());
      mi.outData.put("RGTM",container.get("EXRGTM").toString());
      mi.outData.put("TMSX",container.get("EXTMSX").toString());
      mi.outData.put("NOTB",container.get("EXNOTB").toString());
      mi.outData.put("SPKG",container.get("EXSPKG").toString());

      mi.write();
    }

  }

}
