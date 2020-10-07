package com.belfoapps.qarib.ui.custom;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.belfoapps.qarib.R;
import com.belfoapps.qarib.databinding.ContactsDialogBinding;
import com.belfoapps.qarib.databinding.LocationDialogBinding;
import com.belfoapps.qarib.databinding.SocialMediaDialogBinding;
import com.belfoapps.qarib.databinding.WebsiteDialogBinding;
import com.belfoapps.qarib.pojo.Post;
import com.belfoapps.qarib.utils.ResourcesUtils;
import com.belfoapps.qarib.utils.Types;
import com.google.android.material.snackbar.Snackbar;
import com.huawei.hmf.tasks.OnCompleteListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationServices;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreatePostDialog extends DialogFragment {
    private static final String TAG = "PostDialog";
    public static final String TYPE = "type";

    public interface PostCreationListener {

        void createPost(Post post);

    }

    /***********************************************************************************************
     * *********************************** Declarations
     */
    private PostCreationListener listener;
    private ContactsDialogBinding cBinding;
    private SocialMediaDialogBinding smBinding;
    private WebsiteDialogBinding wBinding;
    private LocationDialogBinding lBinding;
    private String type;

    /***********************************************************************************************
     * *********************************** Constructor
     */
    public CreatePostDialog() {
    }

    public CreatePostDialog(String type) {
        this.type = type;
    }

    /***********************************************************************************************
     * *********************************** LifeCycle
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            listener = (PostCreationListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling fragment must implement Callback interface");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        Dialog dialog = null;

        if (savedInstanceState != null)
            type = savedInstanceState.getString(TYPE);

        switch (Types.valueOf(type.replaceAll("\\s+", ""))) {
            case Contacts:
                dialog = getContactsDialog(savedInstanceState, builder, inflater);
                break;
            case SocialMedia:
                dialog = getSocialMediaDialog(savedInstanceState, builder, inflater);
                break;
            case Website:
                dialog = getWebsiteDialog(savedInstanceState, builder, inflater);
                break;
            case Location:
                dialog = getLocationDialog(savedInstanceState, builder, inflater);
                break;
        }

        //Make Transparent Background
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return dialog;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TYPE, type);

        if (cBinding != null) {
            outState.putString("Title", cBinding.contactsTitle.getEditText().getText().toString());
            outState.putString("Content", cBinding.contactsContent.getEditText().getText().toString());
            outState.putString("Email", cBinding.contactsEmail.getEditText().getText().toString());
            outState.putString("Phone", cBinding.contactsPhone.getEditText().getText().toString());

        } else if (smBinding != null) {
            outState.putString("Title", smBinding.socialMediaTitle.getEditText().getText().toString());
            outState.putString("Content", smBinding.socialMediaContent.getEditText().getText().toString());

        } else if (wBinding != null) {
            outState.putString("Title", wBinding.websiteTitle.getEditText().getText().toString());
            outState.putString("Content", wBinding.websiteContent.getEditText().getText().toString());
            outState.putString("Website", wBinding.website.getEditText().getText().toString());
        }
    }

    private Dialog getContactsDialog(Bundle savedInstanceState, AlertDialog.Builder builder, LayoutInflater inflater) {

        //Set ViewBinding
        cBinding = ContactsDialogBinding.inflate(inflater, null,
                false);

        //Add State
        if (savedInstanceState != null) {
            cBinding.contactsTitle.getEditText().setText(savedInstanceState.getString("Title"));
            cBinding.contactsContent.getEditText().setText(savedInstanceState.getString("Content"));
            cBinding.contactsEmail.getEditText().setText(savedInstanceState.getString("Email"));
            cBinding.contactsPhone.getEditText().setText(savedInstanceState.getString("Phone"));
        }

        //Create Post
        cBinding.createPost.setOnClickListener(v -> {
            cBinding.contactsTitle.setError(null);
            cBinding.contactsContent.setError(null);
            cBinding.contactsEmail.setError(null);
            cBinding.contactsPhone.setError(null);

            if (cBinding.contactsTitle.getEditText().getText().toString().equals(""))
                cBinding.contactsTitle.setError(getResources().getString(R.string.title_error));
            else if (cBinding.contactsContent.getEditText().getText().toString().equals(""))
                cBinding.contactsContent.setError(getResources().getString(R.string.content_error));
            else if (cBinding.contactsEmail.getEditText().getText().toString().equals(""))
                cBinding.contactsEmail.setError(getResources().getString(R.string.email_error));
            else if (!isEmailValid(cBinding.contactsEmail.getEditText().getText().toString()))
                cBinding.contactsEmail.setError(getResources().getString(R.string.email_not_valid));
            else if (cBinding.contactsPhone.getEditText().getText().toString().equals(""))
                cBinding.contactsPhone.setError(getResources().getString(R.string.phone_error));
            else {
                Post post = new Post(null, cBinding.contactsContent.getEditText().getText().toString(),
                        System.currentTimeMillis());
                post.setTitle(cBinding.contactsTitle.getEditText().getText().toString());
                post.setEmail(cBinding.contactsEmail.getEditText().getText().toString());
                post.setPhone(cBinding.contactsPhone.getEditText().getText().toString());

                listener.createPost(post);
            }
        });

        //Close Listener
        cBinding.close.setOnClickListener(v -> dismiss());

        //Request focust
        cBinding.contactsTitle.getEditText().requestFocus();

        return builder.setView(cBinding.getRoot()).create();
    }

    private Dialog getSocialMediaDialog(Bundle savedInstanceState, AlertDialog.Builder builder, LayoutInflater inflater) {

        //Set ViewBinding
        smBinding = SocialMediaDialogBinding.inflate(inflater, null,
                false);

        //Set State
        if (savedInstanceState != null) {
            smBinding.socialMediaTitle.getEditText().setText(savedInstanceState.getString("Title"));
            smBinding.socialMediaContent.getEditText().setText(savedInstanceState.getString("Title"));
        }

        //Create Post
        smBinding.createPost.setOnClickListener(v -> {

            smBinding.socialMediaTitle.getEditText().setError(null);
            smBinding.socialMediaContent.getEditText().setError(null);

            if (smBinding.socialMediaTitle.getEditText().getText().toString().equals(""))
                smBinding.socialMediaTitle.setError(getResources().getString(R.string.title_error));
            else if (smBinding.socialMediaContent.getEditText().getText().toString().equals(""))
                smBinding.socialMediaContent.setError(getResources().getString(R.string.content_error));
            else {
                Post post = new Post(null, smBinding.socialMediaContent.getEditText().getText().toString(),
                        System.currentTimeMillis());
                post.setTitle(smBinding.socialMediaTitle.getEditText().getText().toString());

                listener.createPost(post);
            }

        });

        //Close Listener
        smBinding.close.setOnClickListener(v -> dismiss());

        //Request focust
        smBinding.socialMediaTitle.getEditText().requestFocus();

        return builder.setView(smBinding.getRoot()).create();
    }

    private Dialog getWebsiteDialog(Bundle savedInstanceState, AlertDialog.Builder builder, LayoutInflater inflater) {

        //Set ViewBinding
        wBinding = WebsiteDialogBinding.inflate(inflater, null,
                false);

        //Set State
        if (savedInstanceState != null) {
            wBinding.websiteTitle.getEditText().setText(savedInstanceState.getString("Title"));
            wBinding.websiteContent.getEditText().setText(savedInstanceState.getString("Content"));
            wBinding.website.getEditText().setText(savedInstanceState.getString("Website"));
        }

        //Create Post
        wBinding.createPost.setOnClickListener(v -> {
            wBinding.websiteTitle.getEditText().setError(null);
            wBinding.websiteContent.getEditText().setError(null);
            wBinding.website.getEditText().setError(null);

            if (wBinding.websiteTitle.getEditText().getText().toString().equals(""))
                wBinding.websiteTitle.setError(getResources().getString(R.string.title_error));
            else if (wBinding.websiteContent.getEditText().getText().toString().equals(""))
                wBinding.websiteContent.setError(getResources().getString(R.string.content_error));
            else if (wBinding.website.getEditText().getText().toString().equals(""))
                wBinding.website.setError(getResources().getString(R.string.website_error));
            else if (websiteNonValid(wBinding.website.getEditText().getText().toString()))
                wBinding.website.setError(getResources().getString(R.string.website_not_valid));
            else {
                Post post = new Post(null, wBinding.websiteContent.getEditText().getText().toString(),
                        System.currentTimeMillis());
                post.setTitle(wBinding.websiteTitle.getEditText().getText().toString());
                post.setWebsite(wBinding.website.getEditText().getText().toString());

                listener.createPost(post);
            }
        });

        //Close Listener
        wBinding.close.setOnClickListener(v -> dismiss());

        //Request focust
        wBinding.websiteTitle.getEditText().requestFocus();

        return builder.setView(wBinding.getRoot()).create();
    }

    private Dialog getLocationDialog(Bundle savedInstanceState, AlertDialog.Builder builder, LayoutInflater inflater) {

        final String[] location = {null};

        //Set ViewBinding
        lBinding = LocationDialogBinding.inflate(inflater, null,
                false);

        //Set State
        if (savedInstanceState != null) {
            wBinding.websiteTitle.getEditText().setText(savedInstanceState.getString("Title"));
            wBinding.websiteContent.getEditText().setText(savedInstanceState.getString("Content"));
            wBinding.website.getEditText().setText(savedInstanceState.getString("Website"));
        }

        //Get Location
        lBinding.getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ResourcesUtils.isLocationEnabled(getContext()))
                    Snackbar.make(v, "You should turn on Location",
                            Snackbar.LENGTH_LONG)
                            .setAction("Turn On", vv -> ResourcesUtils.enableLocation(getContext())).show();

                FusedLocationProviderClient fusedLocationProviderClient = LocationServices
                        .getFusedLocationProviderClient(getContext());
                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(Task<Location> task) {
                        if (task.isSuccessful())
                            location[0] = task.getResult().toString();
                        else Toast.makeText(getContext(), "Couldn't Get the Location " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        //Create Post
        lBinding.createPost.setOnClickListener(v -> {
            lBinding.locationTitle.getEditText().setError(null);
            lBinding.locationContent.getEditText().setError(null);

            if (lBinding.locationTitle.getEditText().getText().toString().equals(""))
                lBinding.locationTitle.setError(getResources().getString(R.string.title_error));
            else if (lBinding.locationContent.getEditText().getText().toString().equals(""))
                lBinding.locationContent.setError(getResources().getString(R.string.content_error));
            else if (location[0] == null)
                lBinding.getLocation.setError(getResources().getString(R.string.location_error));
            else if (websiteNonValid(wBinding.website.getEditText().getText().toString()))
                wBinding.website.setError(getResources().getString(R.string.website_not_valid));
            else {
                Post post = new Post(null, wBinding.websiteContent.getEditText().getText().toString(),
                        System.currentTimeMillis());
                post.setTitle(wBinding.websiteTitle.getEditText().getText().toString());
                post.setWebsite(wBinding.website.getEditText().getText().toString());

                listener.createPost(post);
            }
        });

        //Close Listener
        lBinding.close.setOnClickListener(v -> dismiss());

        //Request focust
        lBinding.locationTitle.getEditText().requestFocus();

        return builder.setView(lBinding.getRoot()).create();
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean websiteNonValid(String website) {
        Log.d(TAG, "websiteNonValid: " + website.split(".").length);
        return website.split("\\.").length != 2;
    }
}
