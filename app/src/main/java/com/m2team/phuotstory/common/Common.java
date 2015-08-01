package com.m2team.phuotstory.common;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.m2team.phuotstory.model.DaoMaster;
import com.m2team.phuotstory.model.DaoSession;
import com.m2team.phuotstory.model.MyLocation;
import com.m2team.phuotstory.model.Story;
import com.m2team.phuotstory.model.StoryDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hoang Minh on 7/28/2015.
 */
public class Common {
    private static final int TWO_MINUTES = 1000 * 60 * 2;

    /**
     * Determines whether one Location reading is better than the current Location fix
     *
     * @param location            The new Location that you want to evaluate
     * @param currentBestLocation The current Location fix, to which you want to compare the new one
     */
    protected static boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private static boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public static StoryDao getStoryDAO(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, Constant.DATABASE_NAME, null);
        SQLiteDatabase writableDatabase = helper.getWritableDatabase();
        DaoMaster master = new DaoMaster(writableDatabase);
        DaoSession session = master.newSession();
        StoryDao storyDao = session.getStoryDao();
        return storyDao;
    }

    public static long insertStory(Context context, Story story) {
        StoryDao storyDAO = getStoryDAO(context);
        return storyDAO.insert(story);
    }


    public static List<Story> queryStories(Context context) {
        StoryDao storyDAO = getStoryDAO(context);
        return storyDAO.queryBuilder().orderDesc(StoryDao.Properties.CreatedTime).list();
    }

    public static Story findStory(Context context, long id) {
        StoryDao storyDAO = getStoryDAO(context);
        List<Story> stories = storyDAO.queryRaw(StoryDao.Properties.Id + " = ? " + id);
        if (stories.size() == 0) return null;
        return stories.get(0);
    }


    public static String objToString(ArrayList<MyLocation> locations) {
        return TextUtils.join(Constant.TOKEN_EACH_LOCATION, locations);
    }

    public static ArrayList<MyLocation> stringToObj(String locationsStr) {
        ArrayList<MyLocation> locations = new ArrayList<>();
        String[] split = locationsStr.split(Constant.TOKEN_EACH_LOCATION);
        String[] temp;
        if (split.length > 0) {
            for (int i = 0; i < split.length; i++) {
                String eachLocation = split[i];
                temp = eachLocation.split(Constant.TOKEN_EACH_VALUE);
                if (temp.length == 3) {
                    locations.add(new MyLocation(Double.parseDouble(temp[0]), Double.parseDouble(temp[1]), temp[2]));
                }
            }
        }
        return locations;
    }


    public static String getAddress(Address address) {
        ArrayList<String> addressFragments = new ArrayList<>();
        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
            addressFragments.add(address.getAddressLine(i));
        }
        return TextUtils.join(Constant.COMMA + " ",
                addressFragments);
    }

    public static ArrayList<String> getShortAddress(ArrayList<MyLocation> locations) {
        ArrayList<String> shortAddress = new ArrayList<>();
        if (locations != null && locations.size() > 0) {
            for (int i = 0; i < locations.size() - 1; i++) {
                String address1 = locations.get(i).getAddress();
                String address2 = locations.get(i + 1).getAddress();
                String[] s = address1.split(Constant.COMMA);
                String[] e = address2.split(Constant.COMMA);
                int sLength = s.length - 1;
                int eLength = e.length - 1;
                if (isInVietnam(locations.get(i)) && isInVietnam(locations.get(i + 1))) {
                    while (s[sLength].equalsIgnoreCase(e[eLength]) && sLength >= 0 && eLength >= 0) {
                        s[sLength] = "";
                        e[eLength] = "";
                        sLength--;
                        eLength--;
                    }
                    shortAddress.add(TextUtils.join(Constant.COMMA + " ", s));
                    shortAddress.add(TextUtils.join(Constant.COMMA + " ", e));
                } else {
                    if (s.length >= 2)
                    shortAddress.add(s[s.length - 2] + ", " + s[s.length - 1]);
                    if (e.length >= 2)
                    shortAddress.add(e[e.length - 2] + ", " + e[e.length - 1]);
                }
            }
        }
        return shortAddress;
    }

    public static boolean isInVietnam(MyLocation location) {
        return ((location.getLat() > 8 && location.getLat() < 24) && (location.getLng() > 102 && location.getLng() < 109));
    }
}
