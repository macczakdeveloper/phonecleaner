package android.content.pm;

import android.os.IBinder;

public interface IPackageStatsObserver extends android.os.IInterface{
/** Local-side IPC implementation stub class. */
	
public static abstract class Stub extends android.os.Binder implements IPackageStatsObserver{
	 private static final String DESCRIPTOR = "android.content.pm.IPackageStatsObserver";
	 
	 /** Construct the stub at attach it to the interface. */
	 public Stub(){
		 this.attachInterface(this, DESCRIPTOR);
	 }
		 
	/**
	 * Cast an IBinder object into an IPackageStatsObserver interface,
	 * generating a proxy if needed.
	 */
	 public static IPackageStatsObserver asInterface(IBinder obj){
			 if ((obj==null)) {
				 	return null;
			 }
			 
			 android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
			 if (((iin!=null)&&(iin instanceof IPackageStatsObserver))) {
				 return ((IPackageStatsObserver)iin);
			 }
		 return new Proxy(obj);
	 }
	 
	 public IBinder asBinder(){
		 return this;
	 }
	 
	 public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException{
		 switch (code){
		 	case INTERFACE_TRANSACTION:{
		 		reply.writeString(DESCRIPTOR);
		 		return true;
		 	}
		 	
		 	case TRANSACTION_onGetStatsCompleted:{
		 		data.enforceInterface(DESCRIPTOR);
		 		PackageStats _arg0;
		 		
		 		if ((0!=data.readInt())) {
		 			_arg0 = PackageStats.CREATOR.createFromParcel(data);
		 		} else {
		 			_arg0 = null;
		 		}
		 		
		 		boolean _arg1;
		 		_arg1 = (0!=data.readInt());
		 		this.onGetStatsCompleted(_arg0, _arg1);
		 	return true;
		  }
}
		 
		 return super.onTransact(code, data, reply, flags);
}
	 
	 private static class Proxy implements IPackageStatsObserver{
		 private IBinder mRemote;
		 Proxy(IBinder remote){
			 mRemote = remote;
		 }
		 
		 public IBinder asBinder(){
			 return mRemote;
		 }
		 
		 @SuppressWarnings("unused")
		public String getInterfaceDescriptor(){
			 return DESCRIPTOR;
		 }
		 
		 public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws android.os.RemoteException{
			 android.os.Parcel _data = android.os.Parcel.obtain();
			 
		
			 try {
				 _data.writeInterfaceToken(DESCRIPTOR);
				 if ((pStats!=null)) {
					 _data.writeInt(1);
					 pStats.writeToParcel(_data, 0);
				 }
				 else {
					 _data.writeInt(0);
				 }
				 
				 _data.writeInt(((succeeded)?(1):(0)));
				 mRemote.transact(Stub.TRANSACTION_onGetStatsCompleted, _data, null, IBinder.FLAG_ONEWAY);
			 }
			 finally {
				 _data.recycle();
			 }
		 }
	 }
	 static final int TRANSACTION_onGetStatsCompleted = (IBinder.FIRST_CALL_TRANSACTION + 0);
	}
	public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws android.os.RemoteException;
}
