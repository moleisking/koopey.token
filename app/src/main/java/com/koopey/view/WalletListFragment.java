package com.koopey.view;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.koopey.R;
import com.koopey.common.SerializeHelper;
import com.koopey.controller.GetJSON;
import com.koopey.controller.PostJSON;
import com.koopey.controller.WalletAdapter;
import com.koopey.model.Alert;
import com.koopey.model.Bitcoin;
import com.koopey.model.Ethereum;
import com.koopey.model.AuthUser;
import com.koopey.model.Wallet;
import com.koopey.model.Wallets;

/**
 * Created by Scott on 28/09/2017.
 */

public class WalletListFragment extends ListFragment implements GetJSON.GetResponseListener,  PostJSON.PostResponseListener, View.OnTouchListener {

    private final String LOG_HEADER = "WALLET:LIST";
    private final int WALLET_LIST_FRAGMENT = 369;
    private Bitcoin bitcoin = new Bitcoin();
    private Ethereum ethereum = new Ethereum();
    private FragmentManager fragmentManager;
    private AuthUser authUser = new AuthUser();
    private WalletDialogFragment walletDialogFragment = new WalletDialogFragment();
    private WalletAdapter walletAdapter;
    private Wallets wallets = new Wallets();
    private boolean showScrollbars = true;
    private boolean showValues = true;
    private boolean showImages = true;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (this.getActivity().getIntent().hasExtra("wallets")) {
            this.wallets = (Wallets) this.getActivity().getIntent().getSerializableExtra("wallets");
            this.wallets.getTokoWallet().name = this.authUser.id;
            this.populateWallets();
        } else if (SerializeHelper.hasFile(this.getActivity(), Wallets.WALLETS_FILE_NAME)) {
            this.wallets = (Wallets) SerializeHelper.loadObject(this.getActivity(), Wallets.WALLETS_FILE_NAME);
            this.populateWallets();
        } else {
            this.wallets = new Wallets();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(this.showScrollbars == true) {
            ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.label_wallets));
            ((MainActivity) getActivity()).hideKeyboard();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.authUser = ((MainActivity) getActivity()).getAuthUserFromFile();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wallets, container, false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(activity, attrs, savedInstanceState);
        TypedArray a = activity.obtainStyledAttributes(attrs, R.styleable.ParentChildListFragment);
        this.showScrollbars = a.getBoolean(R.styleable.ParentChildListFragment_showScrollbars, true);
        this.showImages = a.getBoolean(R.styleable.ParentChildListFragment_showImages, true);
        this.showValues = a.getBoolean(R.styleable.ParentChildListFragment_showValues, true);
        a.recycle();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Wallet wallet = this.wallets.get(position);
        ((MainActivity) getActivity()).showWalletReadFragment(wallet);
    }

    @Override
    public void onGetResponse(String output) {
        try {
            String header = (output.length() >= 20) ? output.substring(0, 19).toLowerCase() : output;
            if (header.contains("alert")) {
                Alert alert = new Alert();
                alert.parseJSON(output);
                if (alert.isError()) {
                    this.authUser.wallets.getBitcoinWallet().value = 0.0;
                    this.authUser.wallets.getEthereumWallet().value = 0.0;
                    Toast.makeText(this.getActivity(), getResources().getString(R.string.error_update), Toast.LENGTH_SHORT).show();
                } else if (alert.isSuccess()) {
                    Toast.makeText(this.getActivity(), getResources().getString(R.string.info_update), Toast.LENGTH_SHORT).show();
                }
            } else if (output.contains("wallets")) {
                this.wallets = new Wallets();
                this.wallets.parseJSON(output);
                SerializeHelper.saveObject(this.getActivity(), wallets);
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    @Override
    public void onPostResponse(String output) {
        try {
            String header = (output.length() >= 20) ? output.substring(0, 19).toLowerCase() : output;
            if (header.contains("alert")) {
                Alert alert = new Alert();
                alert.parseJSON(output);
                if (alert.isError()) {
                    this.authUser.wallets.getBitcoinWallet().value = 0.0;
                    this.authUser.wallets.getEthereumWallet().value = 0.0;
                    Toast.makeText(this.getActivity(), getResources().getString(R.string.error_update), Toast.LENGTH_SHORT).show();
                } else if (alert.isSuccess()) {
                    Toast.makeText(this.getActivity(), getResources().getString(R.string.info_update), Toast.LENGTH_SHORT).show();
                }
            } else if (output.contains("bitcoin")) {
                this.bitcoin = new Bitcoin();
                this.bitcoin.parseJSON(output);
                SerializeHelper.saveObject(this.getActivity(), bitcoin);
                //this.txtBitcoinWallet.setText(String.valueOf(bitcoin.amount) + " BTC");
                this.wallets.getBitcoinWallet().value = this.bitcoin.amount;
                //this.setWallets(this.myUser.wallets);
            } else if (output.contains("ethereum")) {
                this.ethereum = new Ethereum();
                this.ethereum.parseJSON(output);
                SerializeHelper.saveObject(this.getActivity(), ethereum);
                this.wallets.getEthereumWallet().value = this.ethereum.balance;
                //this.setWallets(this.myUser.wallets);
                //this.txtEthereumWallet.setText(String.valueOf(ethereum.balance) + " ETH");
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(LOG_HEADER + ":ON:START", "");
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // Disallow the touch request for parent scroll on touch of child view
        v.getParent().requestDisallowInterceptTouchEvent(true);
        return false;
    }

    private void populateWallets() {
        if (getResources().getBoolean(R.bool.transactions)) {
            if (this.wallets != null && !this.wallets.isEmpty()) {
                this.walletAdapter = new WalletAdapter(this.getActivity(), this.wallets, this.showImages, this.showValues);
                this.setListAdapter(walletAdapter);
                if (this.showValues) {
                    this.postBitcoinBalance();
                    this.postEthereumBalance();
                }
                if (this.showScrollbars) {
                    ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.label_wallets));
                    ((MainActivity) getActivity()).hideKeyboard();
                } else {
                    setListViewHeightBasedOnChildren(this.getListView());
                }

            }
        }
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, AbsListView.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    protected void postBitcoinBalance() {
        Wallet bitcoinWallet = this.authUser.wallets.getBitcoinWallet();
        if (bitcoinWallet != null && !bitcoinWallet.name.equals("")) {
            PostJSON asyncTask = new PostJSON(this.getActivity());
            Bitcoin bitcoin = new Bitcoin();
            bitcoin.address = this.authUser.wallets.getBitcoinWallet().name;
            asyncTask.delegate = this;
            asyncTask.execute(this.getString(R.string.get_bitcoin_read_balance),
                    bitcoin.toString(),
                    this.authUser.getToken()); //"{ account :" + myUser.BTCAccount + "}"
        }
    }

    protected void postEthereumBalance() {
        Wallet ethereumWallet = this.authUser.wallets.getEthereumWallet();
        if (ethereumWallet != null && !ethereumWallet.name.equals("")) {
            PostJSON asyncTask = new PostJSON(this.getActivity());
            Ethereum ethereum = new Ethereum();
            ethereum.account = this.authUser.wallets.getEthereumWallet().name;
            asyncTask.delegate = this;
            asyncTask.execute(this.getString(R.string.get_ethereum_read_balance),
                    ethereum.toString(),
                    this.authUser.getToken()); //"{ account :" + myUser.BTCAccount + "}"
        }
    }

    public void setVisibility(int visibility) {
        try {
            this.getListView().setVisibility(visibility);
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    public void setWallets(Wallets wallets) {
        if (wallets != null) {
            this.wallets = wallets;
            this.populateWallets();
        }
    }

    protected void getMyWallets() {
        GetJSON asyncTask = new GetJSON(this.getActivity());
        asyncTask.delegate = this;
        asyncTask.execute(this.getString(R.string.get_wallets_read), "", this.authUser.getToken());
    }
}