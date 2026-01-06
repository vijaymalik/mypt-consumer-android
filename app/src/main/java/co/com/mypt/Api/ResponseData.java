package co.com.mypt.Api;

import com.android.volley.VolleyError;

public interface ResponseData {

    void response(String data);

    void error(VolleyError error);
}
