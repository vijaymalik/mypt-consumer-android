package co.com.mypt.Api;

import com.android.volley.VolleyError;

import org.json.JSONObject;

public interface JsonResponseData {

  void responseObject(JSONObject response);

  void error(VolleyError error);
}
