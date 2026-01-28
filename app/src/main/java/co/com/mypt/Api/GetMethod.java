package co.com.mypt.Api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
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
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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

import co.com.mypt.R;

public class GetMethod {

    private String url;
    private String token;
    private Context context;
    SharedPreferences sharedPreferences;
    public GetMethod(String url, Context context) {
        this.url = url;
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void startMethod(final ResponseData ResponseData){

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        StringRequest request = new StringRequest(Request.Method.GET, url,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("VOLLEY_RESPONSE",
                            "URL : " + url + "\n" +
                                    "Response : " + response
                    );
                    ResponseData.response(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    JSONObject jsonObject=new JSONObject();
                    jsonObject.put("data",0);
                    jsonObject.put("value","Volley Error");
                    ResponseData.error(error);
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
                    } else if (error instanceof ServerError || error.getCause() instanceof ServerError) {
                      /*  Toast.makeText(context, context.getResources().getString(R.string.not_responding)
                                , Toast.LENGTH_SHORT).show();*/
                        error.printStackTrace();
                    }else if (error instanceof TimeoutError || error.getCause() instanceof SocketTimeoutException
                            || error.getCause() instanceof ConnectTimeoutException
                            || error.getCause() instanceof SocketException
                            || (error.getCause().getMessage() != null
                            && error.getCause().getMessage().contains("Connection timed out"))) {
                        Toast.makeText(context, context.getResources().getString(R.string.timeOut),
                                Toast.LENGTH_SHORT).show();
                    } else if (error instanceof TimeoutError || error.getCause() instanceof SocketTimeoutException
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header=new HashMap<>();
                header.put("Authorization","Bearer "+sharedPreferences.getString(Constants.INSTANCE.getToken(),""));
                Log.e("token",""+sharedPreferences.getString("token", ""));
                return header;
            }
        } ;


        request.setRetryPolicy(new DefaultRetryPolicy( 600*30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Log.d("VOLLEY_REQUEST", "URL : " + url);
        requestQueue.add(request);
    }
}
