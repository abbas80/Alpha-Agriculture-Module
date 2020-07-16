package com.example.farming;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class crop_suggestion extends AppCompatActivity {
    private static final String baseURL = "http://192.168.43.185/";

    public RecyclerView crops;
    public RecyclerView exp;
    public ImageView crop_back;
    public TextView c_n,c_p,c_k;
    String moduleid="";
    double current_p=-1;
    double current_n=-1;
    double current_k=-1;

    double f_current_p=-1;
    double f_current_n=-1;
    double f_current_k=-1;
    public List<crop_detail_model> crop_list=new ArrayList<>();
    List<Sensordatum> sensordatumList=new ArrayList<>();
    List<crops> cropsList=new ArrayList<>();
    List<crops> ans_crops=new ArrayList<>();
    List<crops> ans_crops1=new ArrayList<>();

    double forecast_rainfall=5.6;
    double forecast_temp=18.90;

    Sensordatum current_sensordata;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_suggestion);
        c_n=(TextView)findViewById(R.id.current_n);
        c_p=(TextView)findViewById(R.id.current_p);
        c_k=(TextView)findViewById(R.id.current_k);
        cropsList.add(new crops("Wheat",new crop_detail(132,48,102,15.5,25,75,100)));

        cropsList.add(new crops("Rice",new crop_detail(39,20.10,10.80,22,32,150,300)));

        cropsList.add(new crops("Millet",new crop_detail(56,16,16,20,32,40,60)));

        cropsList.add(new crops("SugarCane",new crop_detail(85,59.5,153,21,27,75,150)));

        cropsList.add(new crops("Cotton",new crop_detail(128,56,76,15,35,50,100)));

        cropsList.add(new crops("Jowar",new crop_detail(26,15.60,10.8,25,32,40,100)));

        cropsList.add(new crops("Maize",new crop_detail(60,27.6,73.5,14,27,60,110)));

        cropsList.add(new crops("Urad",new crop_detail(101.67,27.22,85,25,35,65,75)));

        cropsList.add(new crops("Oats",new crop_detail(1920,786.6,1989.3,20,30,8,10)));

        cropsList.add(new crops("Barley",new crop_detail(117.2,46.4,126.8,12,32,80,110)));

        cropsList.add(new crops("Sorgham",new crop_detail(1547,928.2,642.6,20,32,40,100)));

        cropsList.add(new crops("Soyabean",new crop_detail(146,32,74,18,38,30,60)));

        cropsList.add(new crops("Sunflower",new crop_detail(90,27.4,52,20,25,50,70)));

        cropsList.add(new crops("Lentil",new crop_detail(101.67,27.22,85,18,20,40,100)));

        cropsList.add(new crops("Broccoli",new crop_detail(79.2,30.6,75.6,18.33,21.11,80,100)));

        cropsList.add(new crops("Lettuce",new crop_detail(21.6,7.2,45,20,30,100,150)));

        cropsList.add(new crops("Peas",new crop_detail(600,300,177,15,30,40,50)));

        cropsList.add(new crops("Potato",new crop_detail(62,15.5,93.3,14,25,30,50)));

        cropsList.add(new crops("Tomato",new crop_detail(72.8,28,162.4,10,25,40,60)));

        crop_back=(ImageView)findViewById(R.id.crop_back);
        crops=(RecyclerView) findViewById(R.id.crop_suggest_recycler_view);
        Intent i=getIntent();
        moduleid=i.getStringExtra("MODULE_ID");
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURL).client(client)
                .addConverterFactory(GsonConverterFactory.create(new Gson())).build();
        sensordataapi sensordataapi=retrofit.create(com.example.farming.sensordataapi.class);
        Call<List<Sensordatum>> call=sensordataapi.getsensordata("/sensordata.php");
        call.enqueue(new Callback<List<Sensordatum>>() {
            @Override
            public void onResponse(Call<List<Sensordatum>> call, Response<List<Sensordatum>> response) {
                sensordatumList=response.body();
                //Toast.makeText(crop_suggestion.this,"sensor data="+sensordatumList,Toast.LENGTH_LONG).show();
                fillcrop_list();
                intitrecyclerView();
            }

            @Override
            public void onFailure(Call<List<Sensordatum>> call, Throwable t) {

                Toast.makeText(crop_suggestion.this,"ERROR"+t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

        crop_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    public double factor(double x0,double x1,double x2,double x3,double x4,double x5)
    {
        double soil_val=(x1+(x2/x3)*(x4-x5));
        double ans=soil_val>=(x0*0.7)?x0*0.3:x0-soil_val;
        return ans;
    }
    private void intitrecyclerView() {
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        crops.setLayoutManager(linearLayoutManager);
        crop_detail_adapter adapter_rec=new crop_detail_adapter((List<crop_detail_model>) crop_list);
        crops.setAdapter(adapter_rec);
    }

    private void fillcrop_list() {
        for(Sensordatum sensordatum:sensordatumList)
        {
            if(sensordatum.getModuleId().equalsIgnoreCase(moduleid))
            {
                //Toast.makeText(crop_suggestion.this,""+sensordatum.getModuleId(),Toast.LENGTH_LONG).show();
                current_sensordata=new Sensordatum(sensordatum.getModuleId(),sensordatum.getNitrogen(),sensordatum.getPhosphorus(),sensordatum.getPotassium());
                current_n=Double.parseDouble(sensordatum.getNitrogen());
                current_p=Double.parseDouble(sensordatum.getPhosphorus());
                 current_k=Double.parseDouble(sensordatum.getPotassium());
               // Toast.makeText(crop_suggestion.this,"**n--"+current_n+"--p--"+current_p+"--k--"+current_k,Toast.LENGTH_LONG).show();
                f_current_n=current_n;
                 f_current_p=current_p;
                 f_current_k=current_k;

                break;
            }
        }
        //Toast.makeText(crop_suggestion.this,"n--"+current_n+"--p--"+current_p+"--k--"+current_k,Toast.LENGTH_LONG).show();
        current_n=(current_n>2.2&&current_n<=3.8)?1:(current_n>3.8&&current_n<=4.1)?2:(current_n>4.1)?3:-1;
        current_p=(current_p>2.4&&current_p<=2.8)?1:(current_p>2.8&&current_p<=3.3)?2:(current_p>3.3)?3:-1;
        current_k=(current_k>1.6&&current_k<=2.2)?1:(current_k>2.2&&current_k<=2.8)?2:(current_k>2.8)?3:-1;



        for(crops c:cropsList)
        {

            double cmp_temp_min=c.getCrop_detail().getTemp_min();
            double cmp_temp_max=c.getCrop_detail().getTemp_max();
            if(cmp_temp_min<=forecast_temp&&cmp_temp_max>=forecast_temp)
            {
                ans_crops.add(new crops(c.getCrop_name(),new crop_detail(c.getCrop_detail().getCrop_nitrogen(),c.getCrop_detail().getCrop_phosphorus(),c.getCrop_detail().getCrop_potassium(),c.getCrop_detail().getTemp_min(),c.getCrop_detail().getTemp_max(),c.getCrop_detail().getRainfall_min(),c.getCrop_detail().getRainfall_max())));
            }
        }
        for(crops c:ans_crops)
        {
            double temp_n=c.getCrop_detail().getCrop_nitrogen();
            double temp_p=c.getCrop_detail().getCrop_phosphorus();
            double temp_k=c.getCrop_detail().getCrop_potassium();
            temp_n=temp_n<280?1:temp_n>=280&&temp_n<560?2:3;

            temp_p=temp_p<10?1:temp_p>=10&&temp_p<25?2:3;

            temp_k=temp_k<110?1:temp_k>=110&&temp_k<280?2:3;
            if(temp_n==current_n&&temp_p==current_p&&temp_k==current_k||temp_n==current_n&&temp_p==current_p)
            {
                ans_crops1.add(new crops(c.getCrop_name(),new crop_detail(c.getCrop_detail().getCrop_nitrogen(),c.getCrop_detail().getCrop_phosphorus(),c.getCrop_detail().getCrop_potassium(),c.getCrop_detail().getTemp_min(),c.getCrop_detail().getTemp_max(),c.getCrop_detail().getRainfall_min(),c.getCrop_detail().getRainfall_max())));
            }
        }
        double n_temp_fact1=current_n==1?0:current_n==2?280:current_n==3?560:0;

        double n_temp_fact2=current_n==1?280:current_n==2?280:current_n==3?560:0;

        double n_temp_fact3=current_n==1?1.6:current_n==2?0.3:current_n==3?4.1:0;

        double n_temp_fact5=current_n==1?2.2:current_n==2?3.8:current_n==3?4.1:0;


        double p_temp_fact1=current_p==1?0:current_p==2?10:current_p==3?25:0;

        double p_temp_fact2=current_p==1?10:current_p==2?15:current_p==3?25:0;

        double p_temp_fact3=current_p==1?0.4:current_p==2?0.5:current_p==3?3.3:0;

        double p_temp_fact5=current_p==1?2.4:current_p==2?2.8:current_p==3?3.3:0;


        double k_temp_fact1=current_k==1?0:current_k==2?110:current_k==3?280:0;

        double k_temp_fact2=current_k==1?120:current_k==2?170:current_k==3?280:0;

        double k_temp_fact3=current_k==1?0.6:current_k==2?0.6:current_k==3?2.8:0;

        double k_temp_fact5=current_k==1?1.6:current_k==2?2.2:current_k==3?2.8:0;





        c_n.setText(""+(int)(n_temp_fact1+(n_temp_fact2/n_temp_fact3)*(f_current_n-n_temp_fact5)));
        c_p.setText(""+(int)(p_temp_fact1+(p_temp_fact2/p_temp_fact3)*(f_current_p-p_temp_fact5)));
        c_k.setText(""+(int)(k_temp_fact1+(k_temp_fact2/k_temp_fact3)*(f_current_k-k_temp_fact5)));

        for(crops c:ans_crops1)
        {
            int temp_f_current_n=(int)factor(c.getCrop_detail().getCrop_nitrogen(),n_temp_fact1,n_temp_fact2,n_temp_fact3,f_current_n,n_temp_fact5);
            int temp_f_current_p=(int)factor(c.getCrop_detail().getCrop_phosphorus(),p_temp_fact1,p_temp_fact2,p_temp_fact3,f_current_p,p_temp_fact5);
            int temp_f_current_k=(int)factor(c.getCrop_detail().getCrop_potassium(),k_temp_fact1,k_temp_fact2,k_temp_fact3,f_current_k,k_temp_fact5);




            String bullet="~ "+(int)(temp_f_current_n*0.4)+" , "+(int)(temp_f_current_p*0.4)+" , "+(int)(temp_f_current_k*0.4)+" kg/ha(i.e. 40% fertilizer) at the time of sowing seeds\n \n"+"~ "+(int)(temp_f_current_n*0.4*0.6)+" , "+(int)(temp_f_current_p*0.4*0.6)+" , "+(int)(temp_f_current_k*0.4*0.6)+" kg/ha(i.e. 60% of the 40% fertilizer) at the time of vegetative phase(35-45 days)\n\n"+"~ "+(int)(temp_f_current_n*0.4*0.4)+" , "+(int)(temp_f_current_p*0.4*0.4)+" , "+(int)(temp_f_current_k*0.4*0.4)+" kg/ha(i.e.40 % of  the 40% fertilizer) at the time of flowering phase(80-120 days)";
            crop_list.add(new crop_detail_model(0,R.drawable.farm1,c.getCrop_name(),""+temp_f_current_n,""+temp_f_current_p,""+temp_f_current_k,bullet));
        }

        if(crop_list.size()==0)
        {
            crop_list.add(new crop_detail_model(1,R.drawable.farm1,"Wheat","120","60","30","bullet"));
        }

        /*crop_list.add(new crop_detail_model(R.drawable.farm1,"Wheat","120","60","30"));

        crop_list.add(new crop_detail_model(R.drawable.farm1,"Barley","160","80","40"));
        crop_list.add(new crop_detail_model(R.drawable.farm1,"Rice","150","60","30"));
        crop_list.add(new crop_detail_model(R.drawable.farm1,"Corn","80","40","20"));
        crop_list.add(new crop_detail_model(R.drawable.farm1,"Wheat","120","60","30"));*/
    }
}
