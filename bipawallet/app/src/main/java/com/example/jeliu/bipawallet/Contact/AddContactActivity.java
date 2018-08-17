package com.example.jeliu.bipawallet.Contact;

import android.Manifest;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.jeliu.bipawallet.Base.BaseActivity;
import com.example.jeliu.bipawallet.Model.HZContact;
import com.example.jeliu.bipawallet.R;
import com.example.jeliu.bipawallet.UserInfo.UserInfoManager;

import java.lang.reflect.Array;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by liuming on 05/05/2018.
 */

public class AddContactActivity extends BaseActivity {
    private static final int CONTACT_PICKER_RESULT = 1001;
    private static final String DEBUG_TAG = "Contact List";

    @BindView(R.id.imageView_photo)
    ImageView ivPhoto;

    @BindView(R.id.editText_surname)
    EditText etSurname;

    @BindView(R.id.editText_name)
    EditText etName;

    @BindView(R.id.et_address)
    EditText etAddress;

    @BindView(R.id.et_add_phone)
    EditText etPhone;

    @BindView(R.id.et_add_mail)
    EditText etMail;

    @BindView(R.id.iv_contact)
    ImageView ivContact;

    @BindView(R.id.iv_scan)
    ImageView ivScan;


    @OnClick(R.id.imageView_photo) void onAddPhoto() {

    }

    @BindView(R.id.button_copy)
    Button btnCopy;

    @OnClick(R.id.button_copy) void onCopy() {
        ArrayList<HZContact> contacts = UserInfoManager.getInst().getContacts();
        if (contacts != null && index < contacts.size()) {
            HZContact contact = contacts.get(index);
            if (contact != null) {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if (contact.address != null) {
                    cm.setText(contact.address);
                    Toast.makeText(this, getString(R.string.copy_succeed), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.copy_address_error), Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    @OnClick(R.id.iv_scan) void onScan() {
        scanCode();
    }

    private boolean check;
    private int index;

    @OnClick(R.id.iv_contact) void onContact() {
        if (!checkPermission(Manifest.permission.READ_CONTACTS)) {
            Toast.makeText(this, getString(R.string.open_contact), Toast.LENGTH_SHORT).show();
            return;
        }

        Intent it = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);

        startActivityForResult(it, CONTACT_PICKER_RESULT);
    }

    protected void scanDone(String barcode) {
        if (!checkAddress(barcode)) {
        return;
    }
        etAddress.setText(retrieveAddress(barcode));
    }

    // handle after selecting a contact from the list
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CONTACT_PICKER_RESULT) {
                Uri uri = data.getData();
                Cursor c = getContentResolver().query(uri, null, null, null, null);
                if (c.moveToFirst()) {
                    // String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    String phoneNumber = null;
                    if ( hasPhone.equalsIgnoreCase("1")) {
                        hasPhone = "true";
                    } else {
                        hasPhone = "false" ;
                    }
                    if (Boolean.parseBoolean(hasPhone)) {
                        String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                        Cursor phones= getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId,null, null);
                        while (phones.moveToNext()) {
                            phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            if (phoneNumber != null && phoneNumber.length() > 0) {
                                etPhone.setText(phoneNumber);
                                break;
                            }
                        }
                        phones.close();
                    }
                }
                c.close();
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        } else {
            // gracefully handle failure
            Log.w(DEBUG_TAG, "Warning: activity result not ok");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        check = intent.getBooleanExtra("check", false);

        showBackButton();
        if (!check) {
            setTitle(getString(R.string.new_contact));
            showDone();
            btnCopy.setVisibility(View.GONE);
        } else {
            btnCopy.setVisibility(View.VISIBLE);
            setTitle("");
            index = intent.getIntExtra("index", 0);
            setupView();
            ivContact.setEnabled(false);
            ivScan.setEnabled(false);
        }
    }

    private void setupView() {
        ArrayList<HZContact> contacts = UserInfoManager.getInst().getContacts();
        if (contacts != null && index < contacts.size()) {
            HZContact contact = contacts.get(index);
            if (contact != null) {
                etName.setText(contact.firstname);
                etName.setEnabled(false);
                etSurname.setText(contact.lastname);
                etSurname.setEnabled(false);
                etAddress.setText(contact.address);

                etAddress.setEnabled(false);
                etPhone.setText(contact.phone);
                etPhone.setEnabled(false);
                etMail.setText(contact.mail);
                etMail.setEnabled(false);
            }
        }
    }

    protected void onDone() {
        if (!checkInputs(etName, etSurname, etAddress)) {
            return;
        }
        HZContact contact = new HZContact();
        contact.firstname = etName.getText().toString();
        contact.lastname = etSurname.getText().toString();
        contact.address = etAddress.getText().toString();
        contact.phone = etPhone.getText().toString();
        contact.mail = etMail.getText().toString();
        if (contact.mail.length() > 0) {
            if (!checkMail(contact.mail)) {
                return;
            }
        }
        if (contact.phone.length() > 0) {
            String p = contact.phone;
            contact.phone = handlePhone(p);
            if (!checkPhone(contact.phone)) {
                return;
            }
        }

        UserInfoManager.getInst().insertContact(contact);
        setResult(RESULT_OK);
        finish();
    }
}
