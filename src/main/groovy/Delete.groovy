/**
 * Name:    EXT002MI_Delete
 * Description:
 *          API to delete data from EXTDEL table
 *
 * Usage:
 *  Arguments
 * @CONO  Numric,3        Company
 * @INOU  Numeric,1       Direction
 * @DLIX  Numeric,11      Delivery
 *
 * Created By: Birlasoft Ltd.
 * Authors:    Aditya Bhatkhande  
 *
 * History:
 *  20220201  base script created
 *
 */
public class Delete extends ExtendM3Transaction {
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


  public Delete(MIAPI mi, ProgramAPI program, MICallerAPI miCaller, DatabaseAPI database) {
    this.mi = mi;
    this.program = program;
    this.miCaller = miCaller;
    this.database = database;
  }

  public void main() {

    iCONO = mi.in.get("CONO");
    iINOU = mi.in.get("INOU");
    iDLIX = mi.in.get("DLIX");



    DBAction query = database.table("EXTDEL").index("00").selection("EXCREQ","EXCAMT","EXRGDT","EXLMDT").build();
    DBContainer container = query.getContainer();
    container.set("EXCONO",iCONO);
    container.set("EXINOU",iINOU);
    container.set("EXDLIX",iDLIX);

    // Check if record exists
    if(!query.read(container)){
      mi.error("Record does not exist");
    }
    else{
      query.readLock(container,deleterCallback);
    }


  }

  // Callback for delete operation
  Closure<?> deleterCallback = { LockedResult lockedResult ->
    lockedResult.delete()
  }


}
