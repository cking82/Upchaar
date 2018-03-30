package Fragments;

import android.content.Context;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import client.RestClient;
import in.project.com.upchaar.R;
import models.DoctorUser;
import models.PatientUser;
import models.Return_Signup_User;
import models.SignUpUser;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import services.UpchaarService;

public class PatientSignUp extends DialogFragment {

    private Context context;
    private UpchaarService libraryServiceAPI = RestClient.getClient();
    private PatientUser PatientUserUser;
    DatePicker DOB ;
    Button setDate;
    EditText DOBtext;
    EditText Email;
    EditText Password;
    EditText Username;
    EditText Location;
    Spinner gender;
    Button Submit;

    ArrayList<String> genderArray = new ArrayList<String>(Arrays.asList("M", "F","Other"));
    private class dateOfBirth {
        int date;
        int month;
        int year;
    }
    dateOfBirth dob = new dateOfBirth();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);
        PatientUser PatientUser=new PatientUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_patient_sign_up, container, false);
        DOB = (DatePicker)view.findViewById(R.id.datePicker);
        setDate = (Button)view.findViewById(R.id.set_date);
        DOBtext = (EditText)view.findViewById(R.id.DateOfBirthtext);
        Email = (EditText)view.findViewById(R.id.Email_id);
        Password = (EditText)view.findViewById(R.id.Password);
        Username = (EditText)view.findViewById(R.id.Username);
        Location = (EditText)view.findViewById(R.id.location);
        gender = (Spinner)view.findViewById(R.id.spinner_gender);
        Submit = (Button)view.findViewById(R.id.Submit);


        setDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("datePicker","here");
                DOB.setVisibility(View.VISIBLE);
                DOB.setClickable(true);
                DOBtext.setVisibility(View.INVISIBLE);
                setDate.setVisibility(View.INVISIBLE);
                Email.setVisibility(View.INVISIBLE);
                Password.setVisibility(View.INVISIBLE);
                Username.setVisibility(View.INVISIBLE);
                gender.setVisibility(View.INVISIBLE);
                Location.setVisibility(View.INVISIBLE);
                Submit.setVisibility(View.INVISIBLE);


            }
        });

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        DOB.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        dob.date = dayOfMonth;
                        dob.month = month + 1;
                        dob.year = year;
                        DOBtext.setText("" + dob.date + "/" + dob.month + "/" + dob.year);
                        collapse();
                    }
                });

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SignUpUser signUpUser=new SignUpUser();
                signUpUser.setUsername(Username.getText().toString().trim());
                signUpUser.setFirst_name("Bhavya");
                signUpUser.setLast_name("Gupta");
                signUpUser.setEmail(Email.getText().toString().trim());
                signUpUser.setAddress(Location.getText().toString().trim());
                signUpUser.setRole(1);
                signUpUser.setGender(gender.getTag().toString().trim());

                signUpUser.setDate_of_birth(dob.year+"-"+dob.month+"-"+dob.date);

                Call<Return_Signup_User> signupCall = libraryServiceAPI.signup(signUpUser);
                signupCall.enqueue(new Callback<Return_Signup_User>() {
                    @Override
                    public void onResponse(Call<Return_Signup_User> call, Response<Return_Signup_User> response) {
                        System.out.println(response.code());
                        if (response.isSuccessful()) {
                            Return_Signup_User user = response.body();
                            if (user != null) {
                                System.out.println(user);
                            }
                        } else {

                        }
                    }

                    @Override
                    public void onFailure(Call<Return_Signup_User> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
            }
        });

        final CustomSpinnerAdapter genderAdapter = new CustomSpinnerAdapter(this.getActivity(), R.layout.spinner_item, genderArray, "Gender");
        gender.setAdapter(genderAdapter);

        return view;


    }

    public void collapse(){
        DOB.setVisibility(View.INVISIBLE);
        DOB.setClickable(false);
        DOBtext.setVisibility(View.VISIBLE);
        setDate.setVisibility(View.VISIBLE);
        Email.setVisibility(View.VISIBLE);
        Password.setVisibility(View.VISIBLE);
        Username.setVisibility(View.VISIBLE);
        gender.setVisibility(View.VISIBLE);
        Location.setVisibility(View.VISIBLE);
        Submit.setVisibility(View.VISIBLE);
    }

    public class CustomSpinnerAdapter extends ArrayAdapter<String> {

        Context context;
        ArrayList<String> objects;
        String firstElement;
        boolean isFirstTime;

        public CustomSpinnerAdapter(Context context, int textViewResourceId, ArrayList<String> objects, String defaultText) {
            super(context, textViewResourceId, objects);
            this.context = context;
            this.objects = objects;
            this.isFirstTime = true;
            setDefaultText(defaultText);

        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            if(isFirstTime) {
                objects.set(0,firstElement);
                isFirstTime = false;
            }
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            notifyDataSetChanged();
            return getCustomView(position, convertView, parent);
        }

        public void setDefaultText(String defaultText) {
            this.firstElement = objects.get(0);
            objects.set(0,defaultText);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.spinner_item, parent, false);
            TextView label = (TextView) row.findViewById(R.id.spinner_item);
            label.setText(objects.get(position));

            return row;
        }

    }
    @Override
    public void onPause(){
        super.onPause();
    }
}
