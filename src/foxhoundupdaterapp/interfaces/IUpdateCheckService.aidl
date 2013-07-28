package foxhoundupdaterapp.interfaces;
import foxhoundupdaterapp.interfaces.IUpdateCheckServiceCallback;

interface IUpdateCheckService
{    
    void checkForUpdates();
    void registerCallback(in IUpdateCheckServiceCallback cb);
    void unregisterCallback(in IUpdateCheckServiceCallback cb);
}
