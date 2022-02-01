/**
 * Name:    EXT002MI_Get
 * Description:
 *          API to get data from EXTDEL table
 *
 * Usage:
 *  Arguments
 * @CONO  Numric,3        Company
 * @INOU  Numeric,1       Direction
 * @DLIX  Numeric,11      Delivery
 * @CREQ  Numeric,1       Credit Requested
 * @CAMT  Numeric,17      Credit Amount
 * @VSNO  Alphanumeric,30 Vessel number
 * @SLNO  Alphanumeric,30 Seal Number
 * @RGDT  Numeric,8       Entry Date
 * @LMDT  Numeric,8       Change date
 *
 * Created By: Birlasoft Ltd.
 * Authors:    Aditya Bhatkhande  
 *
 * History:
 *  20220201  base script created
 *
 */
public class Get extends ExtendM3Transaction {
  private final MIAPI mi;
  private final ProgramAPI program;
  private final MICallerAPI miCaller;
  private final DatabaseAPI database;

  //Input variables
  private int iCONO;
  private int iINOU;
  private int iDLIX;
  private int iCREQ;
  private double iCAMT;
  private boolean hasError = false;


  public Get(MIAPI mi, ProgramAPI program, MICallerAPI miCaller, DatabaseAPI database) {
    this.mi = mi;
    this.program = program;
    this.miCaller = miCaller;
    this.database = database;
  }

  public void main() {

    iCONO = mi.in.get("CONO");
    iINOU = mi.in.get("INOU");
    iDLIX = mi.in.get("DLIX");



    DBAction query = database.table("EXTDEL").index("00").selection("EXCREQ","EXCAMT","EXRGDT","EXLMDT","EXVSNO","EXSLNO").build();
    DBContainer container = query.getContainer();
    container.set("EXCONO",iCONO);
    container.set("EXINOU",iINOU);
    container.set("EXDLIX",iDLIX);

    // Check if the record exists
    if(!query.read(container)){
      mi.error("Record does not exist");
    }
    else{
      mi.outData.put("CONO",container.get("EXCONO").toString());
      mi.outData.put("INOU",container.get("EXINOU").toString());
      mi.outData.put("DLIX",container.get("EXDLIX").toString());
      mi.outData.put("CREQ",container.get("EXCREQ").toString());
      mi.outData.put("CAMT",container.get("EXCAMT").toString());
      mi.outData.put("RGDT",container.get("EXRGDT").toString());
      mi.outData.put("LMDT",container.get("EXLMDT").toString());
      mi.outData.put("VSNO",container.get("EXVSNO").toString());
      mi.outData.put("SLNO",container.get("EXSLNO").toString());

      mi.write();
    }


  }

  Closure <?> callback = {}

}
