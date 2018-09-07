package com.android.plugins;

import android.os.Build;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by JasonYang on 2016/3/11.
 */
public class Permissions extends CordovaPlugin {

    private static final String ACTION_CHECK_PERMISSION = "checkPermission";
    private static final String ACTION_REQUEST_PERMISSION = "requestPermission";
    private static final String ACTION_REQUEST_PERMISSIONS = "requestPermissions";

    private static final int REQUEST_CODE_ENABLE_PERMISSION = 55433;

    private static final String KEY_ERROR = "error";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_RESULT_PERMISSION = "hasPermission";

    private CallbackContext permissionsCallback;

    @Override
    public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if (ACTION_CHECK_PERMISSION.equals(action)) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    checkPermissionAction(callbackContext, args);
                }
            });
            return true;
        } else if (ACTION_REQUEST_PERMISSION.equals(action) || ACTION_REQUEST_PERMISSIONS.equals(action)) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    try {
                        requestPermissionAction(callbackContext, args);
                    } catch (Exception e) {
                        e.printStackTrace();
                        JSONObject returnObj = new JSONObject();
                        addProperty(returnObj, KEY_ERROR, ACTION_REQUEST_PERMISSION);
                        addProperty(returnObj, KEY_MESSAGE, "Request permission has been denied.");
                        callbackContext.error(returnObj);
                        permissionsCallback = null;
                    }
                }
            });
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
        if (permissionsCallback == null) {
            return;
        }

        JSONObject returnObj = new JSONObject();
        if (permissions != null && permissions.length > 0) {
            //Call checkPermission again to verify
            boolean hasAllPermissions = hasAllPermissions(permissions);
            addProperty(returnObj, KEY_RESULT_PERMISSION, hasAllPermissions);
            permissionsCallback.success(returnObj);
        } else {
            addProperty(returnObj, KEY_ERROR, ACTION_REQUEST_PERMISSION);
            addProperty(returnObj, KEY_MESSAGE, "Unknown error.");
            permissionsCallback.error(returnObj);
        }
        permissionsCallback = null;
    }

    private void checkPermissionAction(CallbackContext callbackContext, JSONArray permission) {
        if (permission == null || permission.length() == 0 || permission.length() > 1) {
            JSONObject returnObj = new JSONObject();
            addProperty(returnObj, KEY_ERROR, ACTION_CHECK_PERMISSION);
            addProperty(returnObj, KEY_MESSAGE, "One time one permission only.");
            callbackContext.error(returnObj);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            JSONObject returnObj = new JSONObject();
            addProperty(returnObj, KEY_RESULT_PERMISSION, true);
            callbackContext.success(returnObj);
        } else {
            try {
                JSONObject returnObj = new JSONObject();
                addProperty(returnObj, KEY_RESULT_PERMISSION, cordova.hasPermission(permission.getString(0)));
                callbackContext.success(returnObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void requestPermissionAction(CallbackContext callbackContext, JSONArray permissions) throws Exception {
        if (permissions == null || permissions.length() == 0) {
            JSONObject returnObj = new JSONObject();
            addProperty(returnObj, KEY_ERROR, ACTION_REQUEST_PERMISSION);
            addProperty(returnObj, KEY_MESSAGE, "At least one permission.");
            callbackContext.error(returnObj);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            JSONObject returnObj = new JSONObject();
            addProperty(returnObj, KEY_RESULT_PERMISSION, true);
            callbackContext.success(returnObj);
        } else if (hasAllPermissions(permissions)) {
            JSONObject returnObj = new JSONObject();
            addProperty(returnObj, KEY_RESULT_PERMISSION, true);
            callbackContext.success(returnObj);
        } else {
            permissionsCallback = callbackContext;
            String[] permissionArray = getPermissions(permissions);
            cordova.requestPermissions(this, REQUEST_CODE_ENABLE_PERMISSION, permissionArray);
        }
    }

    private String[] getPermissions(JSONArray permissions) {
        String[] stringArray = new String[permissions.length()];
        for (int i = 0; i < permissions.length(); i++) {
            try {
                stringArray[i] = permissions.getString(i);
            } catch (JSONException ignored) {
                //Believe exception only occurs when adding duplicate keys, so just ignore it
            }
        }
        return stringArray;
    }

    private boolean hasAllPermissions(JSONArray permissions) throws JSONException {
        return hasAllPermissions(getPermissions(permissions));
    }

    private boolean hasAllPermissions(String[] permissions) throws JSONException {

        for (String permission : permissions) {
            if(!cordova.hasPermission(permission)) {
                return false;
            }
        }

        return true;
    }

    private void addProperty(JSONObject obj, String key, Object value) {
        try {
            if (value == null) {
                obj.put(key, JSONObject.NULL);
            } else {
                obj.put(key, value);
            }
        } catch (JSONException ignored) {
            //Believe exception only occurs when adding duplicate keys, so just ignore it
        }
    }
}




'use strict';
module.exports = [
  
  {
    code: 1,
    content: /(['|"|_]?password['|"]?\ *[:|=])^[,|;]{8,}/i,
    caption: 'Potential password in file : password ',
    level: 'medium'
  },
  {
    code: 2,
    content: /(['|"|_]?password['|"]?\ *[:|=])^[,|;]{8,}/i,
    caption: 'Potential password in file : password',
    level: 'medium'
  },
  {
    code: 3,
    content: /(['|"|_]?pw['|"]?\ *[:|=])^[,|;]{8,}/i,
    caption: 'Potential password in file : password',
    level: 'medium'
  },
  {
    code: 4,
    content: /(['|"|_]?pass['|"]?\ *[:|=])^[,|;]{8,}/i,
    caption: 'Potential password in file : password',
    level: 'medium'
  },
  {
    code: 5,
    content: /(['|"|_]?pword['|"]?\ *[:|=])^[,|;]{8,}/i,
    caption: 'Potential password in file : password',
    level: 'medium'
  },
  {
    code: 6,
    content: /(<[^(><.)]+password[^(><.)]+>[^(><.)]+<\/[^(><.)]+password[^(><.)]+>)/i,
    caption: 'Potential password in file : password',
    level: 'medium'
  },
  {
    code: 7,
    content:/(d_?o_?b|date_?|place_? ?of?_? ?birth|birth_?(date|year|month|day))/gi,
    caption: 'Potential PII in file : DOB',
    level: 'medium'
  },
  {
    code: 8,
    content:/((first|middle|last)_? ?name)/gi,
    caption: 'Potential PII in file : Name',
    level: 'medium'
  },
  {
    code: 9,
    content:/email/gi,
    caption: 'Potential PII in file : email',
    level: 'medium'
  },
  {
    code: 10,
    content:/(home)?_? ?address/gi,
    caption: 'Potential PII in file : Home address',
    level: 'medium'
  },
  {
    code: 11,
    content:/home/gi,
    caption: 'Potential PII in file : Home address',
    level: 'medium'
  },
  {
    code: 12,
    content:/(phone)?_? ?number/gi,
    caption: 'Potential PII in file : phone number',
    level: 'medium'
  },
  {
    code: 13,
    content:/phone/gi,
    caption: 'Potential PII in file : phone number',
    level: 'medium'
  },
  {
    code: 15,
    content:/(ethni)?_? ?city/gi,
    caption: 'Potential PII in file : Ethnicity',
    level: 'medium'
  },
  {
    code: 16,
    content:/(racial)?_? ?preference|pref/gi,
    caption: 'Potential PII in file : Racial Preference',
    level: 'medium'
  },
  {
    code: 17,
    content:/(race)?_? ?preference|pref/gi,
    caption: 'Potential PII in file : Racial Preference',
    level: 'medium'
  },
  {
    code: 18,
    content:/(ethnic)?_? ?origin/gi,
    caption: 'Potential PII in file : Ethnicity',
    level: 'medium'
  },
  {
    code: 19,
    content:/(sexual)?_? ?pref/gi,
    caption: 'Potential PII in file : Sexual Preference',
    level: 'medium'
  },
  {
    code: 20,
    content:/(gender)?_? ?pref/gi,
    caption: 'Potential PII in file : Gender',
    level: 'medium'
  },
  {
    code: 21,
    content:/(political)?_? ?opinion/gi,
    caption: 'Potential PII in file : Political opinion',
    level: 'medium'
  },
  {
    code: 22,
    content:/(political)?_? ?preference/gi,
    caption: 'Potential PII in file : Political opinion',
    level: 'medium'
  },
  {
    code: 23,
    content:/(religious)?_? ?belief/gi,
    caption: 'Potential PII in file : religious belief',
    level: 'medium'
  },
  {
    code: 24,
    content:/religion/gi,
    caption: 'Potential PII in file : religion',
    level: 'medium'
  },
  {
    code: 25,
    content:/criminal/gi,
    caption: 'Potential PII in file : criminal record',
    level: 'medium'
  },
  {
    code: 26,
    content:/health/gi,
    caption: 'Potential PII in file : health record',
    level: 'medium'
  },
  {
    code: 27,
    content:/employ/gi,
    caption: 'Potential PII in file : employment details',
    level: 'medium'
  },
  {
    code: 28,
    content:/(t_?f_?n|tax? ?_? ?file? ?_? ?number)/gi,
    caption: 'Potential PII in file : tax file number',
    level: 'medium'
  },
  {
    code: 29,
    content:/(telephone)?_? ?number/gi,
    caption: 'Potential PII in file : telephone number',
    level: 'medium'
  },
  {
    code: 30,
    content:/sign/gi,
    caption: 'Potential PII in file : signature',
    level: 'medium'
  },
  {
    code: 31,
    content:/medical/gi,
    caption: 'Potential PII in file : medical record',
    level: 'medium'
  },
  {
    code: 32,
    content:/(home|_?(street|city|country|pin|post|code))/gi,
    caption: 'Potential PII in file : Home address',
    level: 'medium'
  },
  {
    code: 33,
    content:/(bank|_? ?(account|number|bsb|name))/gi,
    caption: 'Potential PII in file : Bank account',
    level: 'medium'
  },
  {
    code: 34,
    content:/(work|_?(address|street|city|country|pin|post|code))/gi,
    caption: 'Potential PII in file : work address',
    level: 'medium'
  },
  {
    code: 35,
    content:/salary/gi,
    caption: 'Potential PII in file : employment details - salary',
    level: 'medium'
  },
  {
    code: 36,
    content:/(job|_? ?(title))/gi,
    caption: 'Potential password in file : employment details - Job title',
    level: 'medium'
  },
  {
    code: 37,
    content:/(mailing|_? ?(address))/gi,
    caption: 'Potential password in file : mailing address',
    level: 'medium'
  },
  {
    code: 38,
    content:/(billing|_? ?(address))/gi,
    caption: 'Potential password in file : billing address',
    level: 'medium'
  },
  {
    code: 39,
    content:/(driver|_? ?(license))/gi,
    caption: 'Potential password in file : driver license',
    level: 'medium'
  },
  {
    code: 40,
    content:/(passport|_? ?(number))/gi,
    caption: 'Potential password in file : passport number',
    level: 'medium'
  },
  {
    code: 41,
    content:/citizen/gi,
    caption: 'Potential password in file : citizenship details',
    level: 'medium'
  },
  {
    code: 42,
    content:/(marital|_? ?(status))/gi,
    caption: 'Potential password in file : marital status',
    level: 'medium'
  },
  {
    code: 43,
    content:/spouse/gi,
    caption: 'Potential password in file : spouse details',
    level: 'medium'
  },
  {
    code: 44,
    content:/(emergency|_? ?(contact|number))/gi,
    caption: 'Potential password in file : emergency contact details',
    level: 'medium'
  },
​
​
];
