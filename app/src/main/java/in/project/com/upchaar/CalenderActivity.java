package in.project.com.upchaar;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;

import java.util.ArrayList;
import java.util.HashMap;

import Fragments.Day_Details_Fragment;
import client.RestClient;
import models.AppointmentModel;
import models.DaySchedule;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import services.UpchaarService;

public class CalenderActivity extends AppCompatActivity {


    private class Date_appointment {
        int token;
        int upper;

        public int getToken() {
            return token;
        }

        public void setToken(int token) {
            this.token = token;
        }

        public int getUpper() {
            return upper;
        }

        public void setUpper(int upper) {
            this.upper = upper;
        }
    }
    private Button confirm_appointment;
    private Day_Details_Fragment day_details_fragment;
    private CalendarView calendarView;
    ArrayList<AppointmentModel> appointmentModels;
    int doctor_id=0;
    Intent intent;
    HashMap<String,Date_appointment> date_token_mapm=new HashMap<>();
    HashMap<String,Date_appointment> date_token_mapn=new HashMap<>();
    HashMap<String,Date_appointment> date_token_mape=new HashMap<>();
    String global_day;
    String global_month;
    String global_year;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);
        intent=getIntent();
        doctor_id=Integer.parseInt(intent.getStringExtra("did"));
        System.out.println(doctor_id);
        day_details_fragment= (Day_Details_Fragment) getSupportFragmentManager().findFragmentById(R.id.day_details_fragment);
        calendarView= (CalendarView) findViewById(R.id.calendarView);
        confirm_appointment= (Button) findViewById(R.id.confirm_appointment);

        confirm_appointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AppointmentModel appointmentModel=new AppointmentModel();

                int token=1;
                try {
                    token=date_token_mapm.get(global_day).getToken();
                }
                catch (Exception e){
                    token=1;
                }
                appointmentModel.setToken_no(token);
                appointmentModel.setTime_slot_from("09:00:00");
                appointmentModel.setStatus(1);
                appointmentModel.setAppointment_date(global_year+"-"+global_month+"-"+global_day);
                appointmentModel.setHospital(6);
                appointmentModel.setDoctor(doctor_id);
                appointmentModel.setPatient(1);
                UpchaarService libraryServiceAPI = RestClient.getClient();
                Call<AppointmentModel> addBookCall = libraryServiceAPI.register_appoint(appointmentModel);
                final int finalToken = token;
                addBookCall.enqueue(new Callback<AppointmentModel>() {
                    @Override
                    public void onResponse(Call<AppointmentModel> call, Response<AppointmentModel> response) {
                        System.out.println(response.code());


                        Intent intent=new Intent(CalenderActivity.this,AppointmentConfirmed.class);
                        intent.putExtra("date",global_year+"-"+global_month+"-"+global_day);
                        intent.putExtra("token_no", finalToken);
                        startActivity(intent);

                        if (response.isSuccessful()) {
                            AppointmentModel added = response.body();
                            if (added != null) {

                            }
                        } else {

                        }
                    }

                    @Override
                    public void onFailure(Call<AppointmentModel> call, Throwable t) {
                        t.printStackTrace();
                    }
                });


            }
        });

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int d) {
                System.out.println(i+" "+i1+" "+d);
                String day="";

                if(d<=9){
                    day="0"+d;
                }
                else {
                    day= String.valueOf(d);
                }
                global_day=d+"";
                global_month=i1+"";
                global_year=i+"";


                int mt,ms,nt,ns,et,es;
                try {
                    mt = date_token_mapm.get(day).getToken() + 1;
                    ms = 10 - mt;
                }catch (Exception e){
                    mt=1;
                    ms=9;
                }

                try {
                    nt = date_token_mapn.get(day).getToken() + 1;
                    ns = 10 - nt;
                }catch (Exception e){
                    nt=1;
                    ns=9;
                }

                try {
                    et = date_token_mape.get(day).getToken() + 1;
                    es = 10 - et;
                }catch (Exception e){
                    et=1;
                    es=9;
                }

                day_details_fragment.setData(mt,ms,nt,ns,et,es);



            }
        });

        receiveAppointments();

    }

    private void createEachDateData() {

        for(int i=0;i<appointmentModels.size();i++){
            AppointmentModel work=appointmentModels.get(i);
            System.out.print("Here");
            if(work.getDoctor()==doctor_id){
                String day=work.getAppointment_date().split("-")[2];
                String time=work.getTime_slot_from().split(":")[0];
                System.out.println(day+" "+time);

                if(time.equals("09")) {
                    if (!date_token_mapm.containsKey(day)) {
                        Date_appointment date_appointment = new Date_appointment();
                        date_appointment.setUpper(10);
                        date_appointment.setToken(work.getToken_no());
                        date_token_mapm.put(day, date_appointment);
                    } else {
                        if (work.getToken_no() > date_token_mapm.get(day).getToken()) {
                            Date_appointment date_appointment = new Date_appointment();
                            date_appointment.setUpper(10);
                            date_appointment.setToken(work.getToken_no());
                            date_token_mapm.put(day, date_appointment);
                        }
                    }
                }else if(time.equals("18")){
                    if (!date_token_mape.containsKey(day)) {
                        Date_appointment date_appointment = new Date_appointment();
                        date_appointment.setUpper(10);
                        date_appointment.setToken(work.getToken_no());
                        date_token_mape.put(day, date_appointment);
                    } else {
                        if (work.getToken_no() > date_token_mape.get(day).getToken()) {
                            Date_appointment date_appointment = new Date_appointment();
                            date_appointment.setUpper(10);
                            date_appointment.setToken(work.getToken_no());
                            date_token_mape.put(day, date_appointment);
                        }
                    }
                }else{

                    if (!date_token_mapn.containsKey(day)) {
                        Date_appointment date_appointment = new Date_appointment();
                        date_appointment.setUpper(10);
                        date_appointment.setToken(work.getToken_no());
                        date_token_mapn.put(day, date_appointment);
                    } else {
                        if (work.getToken_no() > date_token_mapn.get(day).getToken()) {
                            Date_appointment date_appointment = new Date_appointment();
                            date_appointment.setUpper(10);
                            date_appointment.setToken(work.getToken_no());
                            date_token_mapn.put(day, date_appointment);
                        }
                    }

                }
            }
        }

    }

    private void receiveAppointments() {

        UpchaarService libraryServiceAPI = RestClient.getClient();
        Call<ArrayList<AppointmentModel>> listBooksCall = libraryServiceAPI.listappointment();
        listBooksCall.enqueue(new Callback<ArrayList<AppointmentModel>>() {
            @Override
            public void onResponse(Call<ArrayList<AppointmentModel>> call, Response<ArrayList<AppointmentModel>> response) {
                if (response.isSuccessful()) {
                    appointmentModels=response.body();
                    createEachDateData();

                } else {

                }
            }
            @Override
            public void onFailure(Call<ArrayList<AppointmentModel>> call, Throwable t) {

            }
        });

    }

}
