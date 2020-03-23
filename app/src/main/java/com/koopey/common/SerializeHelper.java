/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.koopey.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.zxing.common.BitMatrix;
import com.koopey.model.Article;
import com.koopey.model.Articles;
import com.koopey.model.Bitcoin;
import com.koopey.model.Ethereum;
import com.koopey.model.Event;
import com.koopey.model.Events;
import com.koopey.model.Game;
import com.koopey.model.Games;
import com.koopey.model.Location;
import com.koopey.model.Locations;
import com.koopey.model.Messages;
import com.koopey.model.AuthUser;
import com.koopey.model.Asset;
import com.koopey.model.Assets;
import com.koopey.model.Review;
import com.koopey.model.Reviews;
import com.koopey.model.Score;
import com.koopey.model.Scores;
import com.koopey.model.Tags;
import com.koopey.model.Transaction;
import com.koopey.model.Transactions;
import com.koopey.model.User;
import com.koopey.model.Users;
import com.koopey.model.Wallet;
import com.koopey.model.Wallets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.util.Date;

public class SerializeHelper {

    private final static String LOG_HEADER = "SERIALIZE:HELPER";

    public static boolean hasFile(Context context, String filename) {
        boolean result = false;
        try {
            File file = context.getFileStreamPath(filename);
            if (file == null || !file.exists()) {
                result = false;
            } else {
                result = true;
            }
        } catch (Exception ioex) {
            result = false;
            Log.d(LOG_HEADER + ":ER", ioex.getMessage());
        } finally {
            return result;
        }
    }

    public static void saveObject(Context context, Object obj) {
        try {
            if ((obj instanceof Article) && !(obj instanceof Article)) {
                FileOutputStream fos = context.openFileOutput(Article.ARTICLE_FILE_NAME, Context.MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(obj);
                os.close();
                fos.close();
            } else            if ((obj instanceof Asset) && !(obj instanceof Asset)) {
                FileOutputStream fos = context.openFileOutput(Asset.ASSET_FILE_NAME, Context.MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(obj);
                os.close();
                fos.close();
            } else if (obj instanceof AuthUser) {
                FileOutputStream fos = context.openFileOutput(AuthUser.AUTH_USER_FILE_NAME, Context.MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(obj);
                os.close();
                fos.close();
            } else if (obj instanceof Articles) {
                if (((Articles) obj).fileType == Assets.MY_ASSETS_FILE_NAME) {
                    FileOutputStream fos = context.openFileOutput(Articles.MY_ARTICLES_FILE_NAME, Context.MODE_PRIVATE);
                    ObjectOutputStream os = new ObjectOutputStream(fos);
                    os.writeObject(obj);
                    os.close();
                    fos.close();
                } else if (((Articles) obj).fileType == Articles.ARTICLE_SEARCH_RESULTS_FILE_NAME) {
                    FileOutputStream fos = context.openFileOutput(Assets.ASSET_SEARCH_RESULTS_FILE_NAME, Context.MODE_PRIVATE);
                    ObjectOutputStream os = new ObjectOutputStream(fos);
                    os.writeObject(obj);
                    os.close();
                    fos.close();
                } else if (((Articles) obj).fileType == Articles.ARTICLE_WATCH_LIST_FILE_NAME) {
                    FileOutputStream fos = context.openFileOutput(Assets.ASSET_WATCH_LIST_FILE_NAME, Context.MODE_PRIVATE);
                    ObjectOutputStream os = new ObjectOutputStream(fos);
                    os.writeObject(obj);
                    os.close();
                    fos.close();
                }
            } else if (obj instanceof Assets) {
                if (((Assets) obj).fileType == Assets.MY_ASSETS_FILE_NAME) {
                    FileOutputStream fos = context.openFileOutput(Assets.MY_ASSETS_FILE_NAME, Context.MODE_PRIVATE);
                    ObjectOutputStream os = new ObjectOutputStream(fos);
                    os.writeObject(obj);
                    os.close();
                    fos.close();
                } else if (((Assets) obj).fileType == Assets.ASSET_SEARCH_RESULTS_FILE_NAME) {
                    FileOutputStream fos = context.openFileOutput(Assets.ASSET_SEARCH_RESULTS_FILE_NAME, Context.MODE_PRIVATE);
                    ObjectOutputStream os = new ObjectOutputStream(fos);
                    os.writeObject(obj);
                    os.close();
                    fos.close();
                } else if (((Assets) obj).fileType == Assets.ASSET_WATCH_LIST_FILE_NAME) {
                    FileOutputStream fos = context.openFileOutput(Assets.ASSET_WATCH_LIST_FILE_NAME, Context.MODE_PRIVATE);
                    ObjectOutputStream os = new ObjectOutputStream(fos);
                    os.writeObject(obj);
                    os.close();
                    fos.close();
                }
            } else if (obj instanceof Bitcoin) {
                FileOutputStream fos = context.openFileOutput(Bitcoin.BITCOIN_FILE_NAME, Context.MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(obj);
                os.close();
                fos.close();
            } else if (obj instanceof Ethereum) {
                FileOutputStream fos = context.openFileOutput(Ethereum.ETHEREUM_FILE_NAME, Context.MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(obj);
                os.close();
                fos.close();
            } else if (obj instanceof Event) {
                FileOutputStream fos = context.openFileOutput(Event.EVENT_FILE_NAME, Context.MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(obj);
                os.close();
                fos.close();
            } else if (obj instanceof Events) {
                FileOutputStream fos = context.openFileOutput(Events.EVENTS_FILE_NAME, Context.MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(obj);
                os.close();
                fos.close();
            } else if (obj instanceof Game) {
                FileOutputStream fos = context.openFileOutput(Game.GAME_FILE_NAME, Context.MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(obj);
                os.close();
                fos.close();
            } else if (obj instanceof Games) {
                FileOutputStream fos = context.openFileOutput(Games.GAMES_FILE_NAME, Context.MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(obj);
                os.close();
                fos.close();
            } else if (obj instanceof Location) {
                FileOutputStream fos = context.openFileOutput(Location.LOCATION_FILE_NAME, Context.MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(obj);
                os.close();
                fos.close();
            } else if (obj instanceof Locations) {
                FileOutputStream fos = context.openFileOutput(Locations.LOCATIONS_FILE_NAME, Context.MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(obj);
                os.close();
                fos.close();
            } else if (obj instanceof Messages) {
                FileOutputStream fos = context.openFileOutput(Messages.MESSAGES_FILE_NAME, Context.MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(obj);
                os.close();
                fos.close();
            } else if (obj instanceof User) {
                FileOutputStream fos = context.openFileOutput(User.USER_FILE_NAME, Context.MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(obj);
                os.close();
                fos.close();
            } else if (obj instanceof Users) {
                FileOutputStream fos = context.openFileOutput(Users.USERS_FILE_NAME, Context.MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(obj);
                os.close();
                fos.close();
            } else if (obj instanceof Review) {
                FileOutputStream fos = context.openFileOutput(Review.REVIEW_FILE_NAME, Context.MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(obj);
                os.close();
                fos.close();
            } else if (obj instanceof Reviews) {
                FileOutputStream fos = context.openFileOutput(Reviews.REVIEWS_FILE_NAME, Context.MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(obj);
                os.close();
                fos.close();
            } else if (obj instanceof Score) {
                FileOutputStream fos = context.openFileOutput(Score.SCORE_FILE_NAME, Context.MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(obj);
                os.close();
                fos.close();
            } else if (obj instanceof Scores) {
                FileOutputStream fos = context.openFileOutput(Scores.SCORES_FILE_NAME, Context.MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(obj);
                os.close();
                fos.close();
            } else if (obj instanceof Tags) {
                FileOutputStream fos = context.openFileOutput(Tags.TAGS_FILE_NAME, Context.MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(obj);
                os.close();
                fos.close();
            } else if (obj instanceof Transaction) {
                FileOutputStream fos = context.openFileOutput(Transaction.TRANSACTION_FILE_NAME, Context.MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(obj);
                os.close();
                fos.close();
            } else if (obj instanceof Transactions) {
                FileOutputStream fos = context.openFileOutput(Transactions.TRANSACTIONS_FILE_NAME, Context.MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(obj);
                os.close();
                fos.close();
            }
            Log.d(LOG_HEADER + ":SAV", "Success");
        } catch (Exception e) {
            Log.d(LOG_HEADER + ":SAV", e.getMessage());
        }
    }

    public static void deleteObject(Context context, String filename) {
        try {
            context.deleteFile(filename);
            Log.d(LOG_HEADER + ":DEL", "Success");
        } catch (Exception e) {
            Log.d(LOG_HEADER + ":DEL", e.getMessage());
        }
    }

    public static Object loadObject(Context context, String filename) {
        Object obj = null;
        try {
            if (filename.equals(Article.ARTICLE_FILE_NAME)) {
                FileInputStream fis = context.openFileInput(filename);
                ObjectInputStream is = new ObjectInputStream(fis);
                obj = (Article) is.readObject();
                is.close();
                fis.close();
            } else             if (filename.equals(Asset.ASSET_FILE_NAME)) {
                FileInputStream fis = context.openFileInput(filename);
                ObjectInputStream is = new ObjectInputStream(fis);
                obj = (Asset) is.readObject();
                is.close();
                fis.close();
            } else if (filename.equals(AuthUser.AUTH_USER_FILE_NAME)) {
                FileInputStream fis = context.openFileInput(filename);
                ObjectInputStream is = new ObjectInputStream(fis);
                obj = (AuthUser) is.readObject();
                is.close();
                fis.close();
            } else if (filename.equals(Bitcoin.BITCOIN_FILE_NAME)) {
                FileInputStream fis = context.openFileInput(filename);
                ObjectInputStream is = new ObjectInputStream(fis);
                obj = (Bitcoin) is.readObject();
                is.close();
                fis.close();
            } else if (filename.equals(Ethereum.ETHEREUM_FILE_NAME)) {
                FileInputStream fis = context.openFileInput(filename);
                ObjectInputStream is = new ObjectInputStream(fis);
                obj = (Ethereum) is.readObject();
                is.close();
                fis.close();
            } else if (filename.equals(Game.GAME_FILE_NAME)) {
                FileInputStream fis = context.openFileInput(filename);
                ObjectInputStream is = new ObjectInputStream(fis);
                obj = (Game) is.readObject();
                is.close();
                fis.close();
            } else if (filename.equals(Games.GAMES_FILE_NAME)) {
                FileInputStream fis = context.openFileInput(filename);
                ObjectInputStream is = new ObjectInputStream(fis);
                obj = (Games) is.readObject();
                is.close();
                fis.close();
            } else if (filename.equals(Location.LOCATION_FILE_NAME)) {
                FileInputStream fis = context.openFileInput(filename);
                ObjectInputStream is = new ObjectInputStream(fis);
                obj = (Location) is.readObject();
                is.close();
                fis.close();
            } else if (filename.equals(Locations.LOCATIONS_FILE_NAME)) {
                FileInputStream fis = context.openFileInput(filename);
                ObjectInputStream is = new ObjectInputStream(fis);
                obj = (Locations) is.readObject();
                is.close();
                fis.close();
            } else if (filename.equals(Messages.MESSAGES_FILE_NAME)) {
                FileInputStream fis = context.openFileInput(filename);
                ObjectInputStream is = new ObjectInputStream(fis);
                obj = (Messages) is.readObject();
                is.close();
                fis.close();
            } else if (filename.equals(Articles.ARTICLE_SEARCH_RESULTS_FILE_NAME) ||
                    filename.equals(Articles.ARTICLE_WATCH_LIST_FILE_NAME) ||
                    filename.equals(Articles.MY_ARTICLES_FILE_NAME)) {
                FileInputStream fis = context.openFileInput(filename);
                ObjectInputStream is = new ObjectInputStream(fis);
                obj = (Articles) is.readObject();
                is.close();
                fis.close();
            } else if (filename.equals(Assets.ASSET_SEARCH_RESULTS_FILE_NAME) ||
                    filename.equals(Assets.ASSET_WATCH_LIST_FILE_NAME) ||
                    filename.equals(Assets.MY_ASSETS_FILE_NAME)) {
                FileInputStream fis = context.openFileInput(filename);
                ObjectInputStream is = new ObjectInputStream(fis);
                obj = (Assets) is.readObject();
                is.close();
                fis.close();
            } else if (filename.equals(Review.REVIEW_FILE_NAME)) {
                FileInputStream fis = context.openFileInput(filename);
                ObjectInputStream is = new ObjectInputStream(fis);
                obj = (Review) is.readObject();
                is.close();
                fis.close();
            } else if (filename.equals(Reviews.REVIEWS_FILE_NAME)) {
                FileInputStream fis = context.openFileInput(filename);
                ObjectInputStream is = new ObjectInputStream(fis);
                obj = (Reviews) is.readObject();
                is.close();
                fis.close();
            } else if (filename.equals(Score.SCORE_FILE_NAME)) {
                FileInputStream fis = context.openFileInput(filename);
                ObjectInputStream is = new ObjectInputStream(fis);
                obj = (Score) is.readObject();
                is.close();
                fis.close();
            } else if (filename.equals(Scores.SCORES_FILE_NAME)) {
                FileInputStream fis = context.openFileInput(filename);
                ObjectInputStream is = new ObjectInputStream(fis);
                obj = (Scores) is.readObject();
                is.close();
                fis.close();
            } else if (filename.equals(Tags.TAGS_FILE_NAME)) {
                FileInputStream fis = context.openFileInput(filename);
                ObjectInputStream is = new ObjectInputStream(fis);
                obj = (Tags) is.readObject();
                is.close();
                fis.close();
            } else if (filename.equals(Transaction.TRANSACTION_FILE_NAME)) {
                FileInputStream fis = context.openFileInput(filename);
                ObjectInputStream is = new ObjectInputStream(fis);
                obj = (Transaction) is.readObject();
                is.close();
                fis.close();
            } else if (filename.equals(Transactions.TRANSACTIONS_FILE_NAME)) {
                FileInputStream fis = context.openFileInput(filename);
                ObjectInputStream is = new ObjectInputStream(fis);
                obj = (Transactions) is.readObject();
                is.close();
                fis.close();
            } else if (filename.equals(User.USER_FILE_NAME)) {
                FileInputStream fis = context.openFileInput(filename);
                ObjectInputStream is = new ObjectInputStream(fis);
                obj = (User) is.readObject();
                is.close();
                fis.close();
            } else if (filename.equals(Users.USERS_FILE_NAME)) {
                FileInputStream fis = context.openFileInput(filename);
                ObjectInputStream is = new ObjectInputStream(fis);
                obj = (Users) is.readObject();
                is.close();
                fis.close();
            } else if (filename.equals(Wallet.WALLET_FILE_NAME)) {
                FileInputStream fis = context.openFileInput(filename);
                ObjectInputStream is = new ObjectInputStream(fis);
                obj = (Wallet) is.readObject();
                is.close();
                fis.close();
            } else if (filename.equals(Wallets.WALLETS_FILE_NAME)) {
                FileInputStream fis = context.openFileInput(filename);
                ObjectInputStream is = new ObjectInputStream(fis);
                obj = (Wallets) is.readObject();
                is.close();
                fis.close();
            }
        } catch (Exception e) {
            Log.d(LOG_HEADER + ":LD", e.getMessage());
        }

        return obj;
    }
}