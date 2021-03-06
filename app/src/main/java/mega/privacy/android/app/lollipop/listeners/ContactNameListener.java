package mega.privacy.android.app.lollipop.listeners;

import android.content.Context;

import mega.privacy.android.app.DatabaseHandler;
import mega.privacy.android.app.lollipop.ManagerActivityLollipop;
import mega.privacy.android.app.lollipop.managerSections.ContactsFragmentLollipop;
import nz.mega.sdk.MegaApiJava;
import nz.mega.sdk.MegaError;
import nz.mega.sdk.MegaRequest;
import nz.mega.sdk.MegaRequestListenerInterface;

import static mega.privacy.android.app.utils.LogUtil.*;

public class ContactNameListener implements MegaRequestListenerInterface {

    Context context;
    DatabaseHandler dbH;

    public ContactNameListener(Context context) {

        this.context = context;
        dbH = DatabaseHandler.getDbHandler(context.getApplicationContext());
    }

    @Override
    public void onRequestStart(MegaApiJava api, MegaRequest request) {
    }

    @Override
    public void onRequestFinish(MegaApiJava api, MegaRequest request, MegaError e) {
        logDebug("onRequestFinish()");

        if (e.getErrorCode() == MegaError.API_OK){

            if(request.getParamType()==MegaApiJava.USER_ATTR_FIRSTNAME){
                logDebug("ManagerActivityLollipop request.getText(): " + request.getText() + " -- " + request.getEmail());
                int rows = dbH.setContactName(request.getText(), request.getEmail());
                logDebug("Rows affected: " + rows);

                ContactsFragmentLollipop cFLol = ((ManagerActivityLollipop)context).getContactsFragment();

                if (cFLol != null){
                        cFLol.updateView();
                }
            }
            else if(request.getParamType()==MegaApiJava.USER_ATTR_LASTNAME){
                logDebug("ManagerActivityLollipop request.getText(): " + request.getText()+" -- " + request.getEmail());
                int rows = dbH.setContactLastName(request.getText(), request.getEmail());
                logDebug("Rows affected: " + rows);

                ContactsFragmentLollipop cFLol = ((ManagerActivityLollipop)context).getContactsFragment();

                if (cFLol != null){
                    cFLol.updateView();
                }
            }
        }
    }

    @Override
    public void onRequestUpdate(MegaApiJava api, MegaRequest request) {
    }

    @Override
    public void onRequestTemporaryError(MegaApiJava api, MegaRequest request, MegaError e) {
    }
}
