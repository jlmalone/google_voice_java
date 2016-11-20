package com.techventus.server.voice;

import com.techventus.server.voice.util.ArrayUtil;

import java.io.IOException;
import java.net.CookieManager;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by josephmalone on 2016-11-18.
 */
public class GVCookieManager extends CookieManager{

    private static final String TAG = GVCookieManager.class.getSimpleName();

    private static volatile GVCookieManager mInstance;

    private static Map<String, String> COOKIE_MAP;


    /**
     * Set the cookie value in the persistent cookie manager.
     * If the cookie map changes, set the updated flag to true so the PersistenceManager can
     * store the values to shared preferences.
     *
     * @param cookieTuple
     * @return
     */
    public void setCookie(final String[] cookieTuple)
    {
        if (!ArrayUtil.isNullOrContainsEmpty(cookieTuple) && cookieTuple.length == 2 && !(COOKIE_MAP.containsKey(cookieTuple[0]) && COOKIE_MAP.get(
                cookieTuple[0]).equals(cookieTuple[1])))
        {
            setCookie(cookieTuple[0], cookieTuple[1]);
        }
    }

    public Map<String, String> getCookies()
    {
        return COOKIE_MAP;
    }

    public void setCookie(final String key, final String value)
    {
        COOKIE_MAP.put(key, value);
    }

    public static GVCookieManager getInstance()
    {
        if (mInstance == null)
        {
            synchronized (GVCookieManager.class)
            {
                if (mInstance == null)
                {
                    mInstance = new GVCookieManager();
                }
            }
        }

        return mInstance;
    }

    private GVCookieManager()
    {
        super();

        if (COOKIE_MAP == null)
        {
            COOKIE_MAP = new HashMap<>();
        }
//		loadMap();
    }
//
//	private void saveMap()
//	{
//		PreferencesManager.getInstance().setAuthToken(mAuthToken);
//		PreferencesManager.getInstance().setSerialisedCookieMap(COOKIE_MAP, true);
//	}
//
//	private void loadMap()
//	{
//		mAuthToken = PreferencesManager.getInstance().getAuthToken();
//		COOKIE_MAP.putAll(PreferencesManager.getInstance().getSerialisedCookieMap());
//	}

    public void clearCookies()
    {
        COOKIE_MAP.clear();
//		PreferencesManager.getInstance().clearCookieMap();
//        saveCookiesIfNeeded();

    }




    private static String[] getCookieKeyValue(final String flatCookieString)
    {
        if (ArrayUtil.isNotEmpty(flatCookieString) && flatCookieString.contains(";"))
        {
            String trimmed = flatCookieString.substring(0, flatCookieString.indexOf(";", 0));
            if (trimmed.contains("="))
            {
                return trimmed.split("=", 2);
            }
        }

        return null;
    }

    @Override
    public Map<String, List<String>> get(final URI uri, final Map<String, List<String>> requestHeaders) throws IOException
    {
        Map<String, List<String>> ret = super.get(uri, requestHeaders);
        return ret;
    }

    @Override
    public void put(final URI uri, final Map<String, List<String>> stringListMap) throws IOException
    {
        super.put(uri, stringListMap);

        if (stringListMap != null && stringListMap.get("Set-Cookie") != null)
        {
            for (String string : stringListMap.get("Set-Cookie"))
            {
                setCookie(getCookieKeyValue(string));
            }
        }
//        saveCookiesIfNeeded();
    }

    public String listAllOutgoingCookies()
    {
        Map<String, String> cookieMap = getCookies();
        StringBuilder sb = new StringBuilder();

        for (String key : cookieMap.keySet())
        {
            sb.append(key + "=").append(cookieMap.get(key)).append("; ");
        }
        return sb.toString();
    }

    public void setCookies(Map<String, String> cookieMap) {
        clearCookies();
        COOKIE_MAP = cookieMap;
    }
}
