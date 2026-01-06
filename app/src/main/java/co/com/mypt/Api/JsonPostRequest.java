package co.com.mypt.Api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import co.com.mypt.R;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

public class JsonPostRequest {

    private String url;
    private JSONObject params;
    private Context context;
    SharedPreferences sharedPreferences;

    public JsonPostRequest(String url, JSONObject params, Context context) {
        this.url = url;
        this.params = params;
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

//    ***************callback for post method**********************

    public void startPostMethod(final JsonResponseData ResponseData){

        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url,params,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        ResponseData.responseObject(response);

                    }
                }, new Response.ErrorListener() {

            @SuppressLint("MissingPermission")
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                ResponseData.error(error);

                try {
                    if(error instanceof NoConnectionError){
                        ConnectivityManager cm =
                                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo activeNetwork = null;
                        if (cm != null) {
                            activeNetwork = cm.getActiveNetworkInfo();
                        }
                        if(activeNetwork != null && activeNetwork.isConnectedOrConnecting()){
                            Toast.makeText(context, context.getResources().getString(R.string.something_went_wrong),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, context.getResources().getString(R.string.no_connection),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else if (error instanceof NetworkError || error.getCause() instanceof ConnectException){
                        Toast.makeText(context, context.getResources().getString(R.string.device_not_connected),
                                Toast.LENGTH_SHORT).show();
                    } else if (error.getCause() instanceof MalformedURLException){
                        Toast.makeText(context, "Bad Request.", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ParseError || error.getCause() instanceof IllegalStateException
                            || error.getCause() instanceof JSONException
                            || error.getCause() instanceof XmlPullParserException){
                        Toast.makeText(context, "Parse Error (because of invalid json or xml).",
                                Toast.LENGTH_SHORT).show();
                    } else if (error.getCause() instanceof OutOfMemoryError){
                        Toast.makeText(context, "Out Of Memory Error.", Toast.LENGTH_SHORT).show();
                    }else if (error instanceof AuthFailureError){
                        Toast.makeText(context, "server couldn't find the authenticated request.",
                                Toast.LENGTH_SHORT).show();
                    } /*else if (error instanceof ServerError || error.getCause() instanceof ServerError) {
                        Toast.makeText(context, context.getResources().getString(R.string.not_responding)
                                , Toast.LENGTH_SHORT).show();
                    }*/else if (error instanceof TimeoutError || error.getCause() instanceof SocketTimeoutException
                            || error.getCause() instanceof ConnectTimeoutException
                            || error.getCause() instanceof SocketException
                            || (error.getCause().getMessage() != null
                            && error.getCause().getMessage().contains("Connection timed out"))) {
                        Toast.makeText(context, context.getResources().getString(R.string.timeOut),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, context.getResources().getString(R.string.unknown_error),
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> header = new HashMap<String, String>();
                header.put("Authorization","Bearer "+sharedPreferences.getString(Constants.INSTANCE.getToken(),""));
                header.put("Content-Type","application/json");
                header.put("X-Requested-With","XMLHttpRequest");
                return header;
            }


        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy( 600*30000,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjReq);
    }
}
