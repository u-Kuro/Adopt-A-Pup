package com.example.adopt_a_pup.view.admin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.adopt_a_pup.R;
import com.example.adopt_a_pup.model.Dog;
import com.example.adopt_a_pup.model.PendingAdoption;
import com.example.adopt_a_pup.model.Person;
import com.example.adopt_a_pup.retrofit.DogApi;
import com.example.adopt_a_pup.retrofit.PendingAdoptionApi;
import com.example.adopt_a_pup.retrofit.RetrofitService;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PendingAdoptionTabFragment extends Fragment {
    public static WeakReference<PendingAdoptionTabFragment> weakActivity;
    Context context;
    final Retrofit retrofit = new RetrofitService().getRetrofit();
    final PendingAdoptionApi pendingAdoptionApi = retrofit.create(PendingAdoptionApi.class);
    final DogApi dogApi = retrofit.create(DogApi.class);
    ListView pendingAdoptionListView;
    PendingAdoptionAdapter pendingAdoptionAdapter;
    ArrayList<PendingAdoption> pendingAdoptions;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        weakActivity = new WeakReference<>(PendingAdoptionTabFragment.this);
        View pendingAdoptionView = inflater.inflate(R.layout.pending_adoption_tab_fragment, container, false);
        pendingAdoptionListView = pendingAdoptionView.findViewById(R.id.pending_adoption_list);
        context = requireContext();
        pendingAdoptionListView.setOnItemClickListener((adapterView, view, i, l) -> {
            if (pendingAdoptionAdapter == null) return;
            PendingAdoption pendingAdoption = pendingAdoptionAdapter.getItem(i);
            managePendingAdoptionBottomDialog(pendingAdoption);
        });
        loadAllPendingAdoptions();
        return pendingAdoptionView;
    }
    public static PendingAdoptionTabFragment getInstanceActivity() {
        if (weakActivity != null) {
            return weakActivity.get();
        } else {
            return null;
        }
    }
    public void loadAllPendingAdoptions() {
        pendingAdoptionApi.getAllPendingAdoptions().enqueue(new Callback<ArrayList<PendingAdoption>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<PendingAdoption>> call, @NonNull Response<ArrayList<PendingAdoption>> response) {
                ArrayList<PendingAdoption> loadedPendingAdoptions = response.body();
                if (loadedPendingAdoptions != null) {
                    pendingAdoptions = loadedPendingAdoptions;
                    if (pendingAdoptionAdapter==null) {
                        pendingAdoptionAdapter = new PendingAdoptionAdapter(context, R.layout.admin_pending_adoption_card, pendingAdoptions);
                        pendingAdoptionListView.setAdapter(pendingAdoptionAdapter);
                    } else {
                        pendingAdoptionAdapter.notifyDataSetChanged();
                    }
                } else {
                    String error = "Error: The response body contains a wrong datatype!";
                    showToast(Toast.makeText(context, error, Toast.LENGTH_SHORT));
                    Logger.getLogger(PendingAdoptionTabFragment.class.getName()).log(Level.SEVERE,"Error Occurred",error);
                }
            }
            @Override
            public void onFailure(@NonNull Call<ArrayList<PendingAdoption>> call, @NonNull Throwable t) {
                showToast(Toast.makeText(context,"Failed to get all pending adoptions!", Toast.LENGTH_SHORT));
                Logger.getLogger(PendingAdoptionTabFragment.class.getName()).log(Level.SEVERE,"Error Occurred",t);
            }
        });
    }

    public void managePendingAdoptionBottomDialog(PendingAdoption pendingAdoption) {
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint("InflateParams") View bottomSheet = inflater.inflate(R.layout.manage_pending_adoptions_drawer, null);
        dialog.setContentView(bottomSheet);

        LinearLayout approveButton = dialog.findViewById(R.id.approve_pending_adoption_button);
        LinearLayout rejectButton = dialog.findViewById(R.id.reject_pending_adoption_button);
        LinearLayout emailAdopterButton = dialog.findViewById(R.id.send_an_email_button);

        System.out.println("asdasddsdasd | "+approveButton+" | "+rejectButton+" | "+emailAdopterButton+" | "+pendingAdoption);
        if (approveButton==null
            || rejectButton==null
            || emailAdopterButton==null
            || pendingAdoption==null
        ) {
            showToast(Toast.makeText(context,"Something went wrong", Toast.LENGTH_SHORT));
            return;
        }

        approveButton.setOnClickListener(view->{
            showToast(Toast.makeText(context,"Please wait", Toast.LENGTH_SHORT));
            pendingAdoptionApi.deletePendingAdoption(pendingAdoption.getId()).enqueue(new Callback<PendingAdoption>() {
                @Override
                public void onResponse(@NonNull Call<PendingAdoption> call, @NonNull Response<PendingAdoption> response) {
                    PendingAdoption deletedPendingAdoption = response.body();
                    if (deletedPendingAdoption==null || pendingAdoption.getId()!=deletedPendingAdoption.getId()) {
                        showToast(Toast.makeText(context,"Failed to approve pending adoptions!", Toast.LENGTH_SHORT));
                    } else {
                        int foundIndex = -1;
                        for (int i = 0; i < pendingAdoptions.size(); i++) {
                            if (pendingAdoptions.get(i).getId() == deletedPendingAdoption.getId()) {
                                foundIndex = i;
                                break;
                            }
                        }
                        if (foundIndex>=0) {
                            pendingAdoptions.remove(foundIndex);
                            if (pendingAdoptionAdapter==null) {
                                pendingAdoptionAdapter = new PendingAdoptionAdapter(context, R.layout.admin_pending_adoption_card, pendingAdoptions);
                                pendingAdoptionListView.setAdapter(pendingAdoptionAdapter);
                            } else {
                                pendingAdoptionAdapter.notifyDataSetChanged();
                            }
                        }
                        dialog.dismiss();
                        showToast(Toast.makeText(context,"Pending adoptions has been approved!", Toast.LENGTH_SHORT));
                        dogApi.deleteDog(deletedPendingAdoption.getDogId()).enqueue(new Callback<Dog>() {
                            @Override
                            public void onResponse(@NonNull Call<Dog> call, @NonNull Response<Dog> response) {
                                Dog deletedDog = response.body();
                                if (deletedDog==null || deletedDog.getId()!=deletedPendingAdoption.getDogId()) {
                                    Logger.getLogger(PendingAdoptionTabFragment.class.getName()).log(Level.SEVERE, "Failed to delete dog record after being adopted!");
                                } else {
                                    DogTabFragment dogTabFragment = DogTabFragment.getInstanceActivity();
                                    if (dogTabFragment != null) {
                                        dogTabFragment.deleteDog(deletedDog);
                                    }
                                }
                            }
                            @Override
                            public void onFailure(@NonNull Call<Dog> call, @NonNull Throwable t) {
                                Logger.getLogger(PendingAdoptionTabFragment.class.getName()).log(Level.SEVERE,"Failed to delete dog record after being adopted!",t);
                            }
                        });
                    }
                }

                @Override
                public void onFailure(@NonNull Call<PendingAdoption> call, @NonNull Throwable t) {
                    showToast(Toast.makeText(context,"Failed to approve pending adoption!", Toast.LENGTH_SHORT));
                    Logger.getLogger(PendingAdoptionTabFragment.class.getName()).log(Level.SEVERE,"Error Occurred",t);
                }
            });
        });
        rejectButton.setOnClickListener(view->{
            showToast(Toast.makeText(context,"Please wait", Toast.LENGTH_SHORT));
            pendingAdoptionApi.deletePendingAdoption(pendingAdoption.getId()).enqueue(new Callback<PendingAdoption>() {
                @Override
                public void onResponse(@NonNull Call<PendingAdoption> call, @NonNull Response<PendingAdoption> response) {
                    PendingAdoption deletedPendingAdoption = response.body();
                    if (deletedPendingAdoption==null || pendingAdoption.getId()!=deletedPendingAdoption.getId()) {
                        showToast(Toast.makeText(context,"Failed to reject pending adoption!", Toast.LENGTH_SHORT));
                    } else {
                        int foundIndex = -1;
                        for (int i = 0; i < pendingAdoptions.size(); i++) {
                            if (pendingAdoptions.get(i).getId() == deletedPendingAdoption.getId()) {
                                foundIndex = i;
                                break;
                            }
                        }
                        if (foundIndex>=0) {
                            pendingAdoptions.remove(foundIndex);
                            if (pendingAdoptionAdapter==null) {
                                pendingAdoptionAdapter = new PendingAdoptionAdapter(context, R.layout.admin_pending_adoption_card, pendingAdoptions);
                                pendingAdoptionListView.setAdapter(pendingAdoptionAdapter);
                            } else {
                                pendingAdoptionAdapter.notifyDataSetChanged();
                            }
                        }
                        dialog.dismiss();
                        showToast(Toast.makeText(context,"Pending adoptions has been rejected!", Toast.LENGTH_SHORT));
                    }
                }
                @Override
                public void onFailure(@NonNull Call<PendingAdoption> call, @NonNull Throwable t) {
                    showToast(Toast.makeText(context,"Failed to reject pending adoption!", Toast.LENGTH_SHORT));
                    Logger.getLogger(PendingAdoptionTabFragment.class.getName()).log(Level.SEVERE,"Error Occurred",t);
                }
            });
        });
        emailAdopterButton.setOnClickListener(view->{
            Person person = pendingAdoption.person;
            if (person==null) {
                showToast(Toast.makeText(context,"Please try again later", Toast.LENGTH_SHORT));
                return;
            }
            String email = person.getEmail();
            if (email==null || email.equals("")) {
                showToast(Toast.makeText(context,"Adopter has no attached email", Toast.LENGTH_SHORT));
                return;
            }
            try {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] { email });
                context.startActivity(Intent.createChooser(intent, ""));
            } catch (android.content.ActivityNotFoundException e) {
                showToast(Toast.makeText(context,"Email application not found", Toast.LENGTH_SHORT));
            }
        });
        dialog.show();
    }

    public void updateDogs() {
        pendingAdoptionAdapter = new PendingAdoptionAdapter(context, R.layout.admin_pending_adoption_card, pendingAdoptions);
        pendingAdoptionListView.setAdapter(pendingAdoptionAdapter);
    }

    public void dogDeleted(long dogId) {
        int foundIndex = -1;
        for (int i = 0; i < pendingAdoptions.size(); i++) {
            if (pendingAdoptions.get(i).getDogId() == dogId) {
                foundIndex = i;
                break;
            }
        }
        if (foundIndex>=0) {
            pendingAdoptions.remove(foundIndex);
            if (pendingAdoptionAdapter==null) {
                pendingAdoptionAdapter = new PendingAdoptionAdapter(context, R.layout.admin_pending_adoption_card, pendingAdoptions);
                pendingAdoptionListView.setAdapter(pendingAdoptionAdapter);
            } else {
                pendingAdoptionAdapter.notifyDataSetChanged();
            }
        }
    }

    public void showToast(Toast toast) {
        AdminActivity adminActivity = AdminActivity.getInstanceActivity();
        if (adminActivity != null) {
            adminActivity.showToast(toast);
        }
    }
}
