package edu.uph.m24si1.foodsaverkelompok4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Foodsaver.db";
    private static final int DATABASE_VERSION = 2;

    // Table Users
    private static final String TABLE_USERS = "users";
    private static final String COL_USER_ID = "uid";
    private static final String COL_USER_NAME = "name";
    private static final String COL_USER_EMAIL = "email";
    private static final String COL_USER_PASSWORD = "password";
    private static final String COL_USER_ROLE = "role";
    private static final String COL_USER_STORE_NAME = "store_name";
    private static final String COL_USER_CREATED_AT = "created_at";

    // Table Foods
    private static final String TABLE_FOODS = "foods";
    private static final String COL_FOOD_ID = "food_id";
    private static final String COL_FOOD_PARTNER_ID = "partner_id";
    private static final String COL_FOOD_NAME = "name";
    private static final String COL_FOOD_DESCRIPTION = "description";
    private static final String COL_FOOD_ORIGINAL_PRICE = "original_price";
    private static final String COL_FOOD_DISCOUNT_PRICE = "discount_price";
    private static final String COL_FOOD_PHOTO_URL = "photo_url";
    private static final String COL_FOOD_QUANTITY = "quantity";
    private static final String COL_FOOD_STATUS = "status";
    private static final String COL_FOOD_PARTNER_NAME = "partner_name";
    private static final String COL_FOOD_ADDRESS = "partner_address";
    private static final String COL_FOOD_CREATED_AT = "created_at";

    // Table Bookings
    private static final String TABLE_BOOKINGS = "bookings";
    private static final String COL_BOOKING_ID = "booking_id";
    private static final String COL_BOOKING_USER_ID = "user_id";
    private static final String COL_BOOKING_FOOD_ID = "food_id";
    private static final String COL_BOOKING_PARTNER_ID = "partner_id";
    private static final String COL_BOOKING_PARTNER_NAME = "partner_name";
    private static final String COL_BOOKING_FOOD_NAME = "food_name";
    private static final String COL_BOOKING_USER_NAME = "user_name";
    private static final String COL_BOOKING_QUANTITY = "quantity";
    private static final String COL_BOOKING_STATUS = "status";
    private static final String COL_BOOKING_DELIVERY_METHOD = "delivery_method";
    private static final String COL_BOOKING_DELIVERY_ADDRESS = "delivery_address";
    private static final String COL_BOOKING_TIME_SLOT = "scheduled_time_slot";
    private static final String COL_BOOKING_TIMESTAMP = "timestamp";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COL_USER_ID + " TEXT PRIMARY KEY,"
                + COL_USER_NAME + " TEXT,"
                + COL_USER_EMAIL + " TEXT UNIQUE,"
                + COL_USER_PASSWORD + " TEXT,"
                + COL_USER_ROLE + " TEXT,"
                + COL_USER_STORE_NAME + " TEXT,"
                + COL_USER_CREATED_AT + " INTEGER" + ")";

        String CREATE_FOODS_TABLE = "CREATE TABLE " + TABLE_FOODS + "("
                + COL_FOOD_ID + " TEXT PRIMARY KEY,"
                + COL_FOOD_PARTNER_ID + " TEXT,"
                + COL_FOOD_NAME + " TEXT,"
                + COL_FOOD_DESCRIPTION + " TEXT,"
                + COL_FOOD_ORIGINAL_PRICE + " REAL,"
                + COL_FOOD_DISCOUNT_PRICE + " REAL,"
                + COL_FOOD_PHOTO_URL + " TEXT,"
                + COL_FOOD_QUANTITY + " INTEGER,"
                + COL_FOOD_STATUS + " TEXT,"
                + COL_FOOD_PARTNER_NAME + " TEXT,"
                + COL_FOOD_ADDRESS + " TEXT,"
                + COL_FOOD_CREATED_AT + " INTEGER" + ")";

        String CREATE_BOOKINGS_TABLE = "CREATE TABLE " + TABLE_BOOKINGS + "("
                + COL_BOOKING_ID + " TEXT PRIMARY KEY,"
                + COL_BOOKING_USER_ID + " TEXT,"
                + COL_BOOKING_FOOD_ID + " TEXT,"
                + COL_BOOKING_PARTNER_ID + " TEXT,"
                + COL_BOOKING_PARTNER_NAME + " TEXT,"
                + COL_BOOKING_FOOD_NAME + " TEXT,"
                + COL_BOOKING_USER_NAME + " TEXT,"
                + COL_BOOKING_QUANTITY + " INTEGER,"
                + COL_BOOKING_STATUS + " TEXT,"
                + COL_BOOKING_DELIVERY_METHOD + " TEXT,"
                + COL_BOOKING_DELIVERY_ADDRESS + " TEXT,"
                + COL_BOOKING_TIME_SLOT + " TEXT,"
                + COL_BOOKING_TIMESTAMP + " INTEGER" + ")";

        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_FOODS_TABLE);
        db.execSQL(CREATE_BOOKINGS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOODS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS);
        onCreate(db);
    }

    // --- User Methods ---

    public boolean registerUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_ID, UUID.randomUUID().toString());
        values.put(COL_USER_NAME, user.getName());
        values.put(COL_USER_EMAIL, user.getEmail());
        values.put(COL_USER_PASSWORD, user.getPassword());
        values.put(COL_USER_ROLE, user.getRole());
        values.put(COL_USER_STORE_NAME, user.getStoreName());
        values.put(COL_USER_CREATED_AT, System.currentTimeMillis());

        long id = db.insert(TABLE_USERS, null, values);
        db.close();
        return id != -1;
    }

    public User loginUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null,
                COL_USER_EMAIL + "=? AND " + COL_USER_PASSWORD + "=?",
                new String[]{email, password}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            User user = cursorToUser(cursor);
            cursor.close();
            return user;
        }
        return null;
    }

    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, COL_USER_EMAIL + "=?",
                new String[]{email}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            User user = cursorToUser(cursor);
            cursor.close();
            return user;
        }
        return null;
    }

    public User getUserById(String uid) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, COL_USER_ID + "=?",
                new String[]{uid}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            User user = cursorToUser(cursor);
            cursor.close();
            return user;
        }
        return null;
    }

    public boolean updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_NAME, user.getName());
        values.put(COL_USER_EMAIL, user.getEmail());
        values.put(COL_USER_ROLE, user.getRole());

        int rows = db.update(TABLE_USERS, values, COL_USER_ID + "=?", new String[]{user.getUid()});
        db.close();
        return rows > 0;
    }

    private User cursorToUser(Cursor cursor) {
        User user = new User();
        user.setUid(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_ID)));
        user.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_NAME)));
        user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_EMAIL)));
        user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_PASSWORD)));
        user.setRole(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_ROLE)));
        user.setStoreName(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_STORE_NAME)));
        user.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(COL_USER_CREATED_AT)));
        return user;
    }

    // --- Food Methods ---

    public boolean insertFood(Food food) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_FOOD_ID, UUID.randomUUID().toString());
        values.put(COL_FOOD_PARTNER_ID, food.getPartnerId());
        values.put(COL_FOOD_NAME, food.getName());
        values.put(COL_FOOD_DESCRIPTION, food.getDescription());
        values.put(COL_FOOD_ORIGINAL_PRICE, food.getOriginalPrice());
        values.put(COL_FOOD_DISCOUNT_PRICE, food.getDiscountPrice());
        values.put(COL_FOOD_PHOTO_URL, food.getPhotoUrl());
        values.put(COL_FOOD_QUANTITY, food.getQuantity());
        values.put(COL_FOOD_STATUS, food.getStatus() == null ? Constants.FOOD_AVAILABLE : food.getStatus());
        values.put(COL_FOOD_PARTNER_NAME, food.getPartnerName());
        values.put(COL_FOOD_ADDRESS, food.getPartnerAddress());
        values.put(COL_FOOD_CREATED_AT, System.currentTimeMillis());

        long id = db.insert(TABLE_FOODS, null, values);
        db.close();
        return id != -1;
    }

    public List<Food> getAllAvailableFoods() {
        List<Food> foods = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FOODS, null,
                COL_FOOD_STATUS + "=? AND " + COL_FOOD_QUANTITY + ">0",
                new String[]{Constants.FOOD_AVAILABLE}, null, null, COL_FOOD_CREATED_AT + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                foods.add(cursorToFood(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return foods;
    }

    public List<Food> getFoodsByPartnerId(String partnerId) {
        List<Food> foods = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FOODS, null, COL_FOOD_PARTNER_ID + "=?",
                new String[]{partnerId}, null, null, COL_FOOD_CREATED_AT + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                foods.add(cursorToFood(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return foods;
    }

    public Food getFoodById(String foodId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FOODS, null, COL_FOOD_ID + "=?",
                new String[]{foodId}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Food food = cursorToFood(cursor);
            cursor.close();
            return food;
        }
        return null;
    }

    public boolean updateFood(Food food) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_FOOD_NAME, food.getName());
        values.put(COL_FOOD_DESCRIPTION, food.getDescription());
        values.put(COL_FOOD_ORIGINAL_PRICE, food.getOriginalPrice());
        values.put(COL_FOOD_DISCOUNT_PRICE, food.getDiscountPrice());
        values.put(COL_FOOD_PHOTO_URL, food.getPhotoUrl());
        values.put(COL_FOOD_QUANTITY, food.getQuantity());
        values.put(COL_FOOD_STATUS, food.getStatus());
        values.put(COL_FOOD_PARTNER_NAME, food.getPartnerName());
        values.put(COL_FOOD_ADDRESS, food.getPartnerAddress());

        int rows = db.update(TABLE_FOODS, values, COL_FOOD_ID + "=?", new String[]{food.getFoodId()});
        db.close();
        return rows > 0;
    }

    public boolean deleteFood(String foodId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_FOODS, COL_FOOD_ID + "=?", new String[]{foodId});
        db.close();
        return rows > 0;
    }

    public boolean reduceQuantity(String foodId, int amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_FOODS, new String[]{COL_FOOD_QUANTITY},
                COL_FOOD_ID + "=?", new String[]{foodId}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int currentQty = cursor.getInt(0);
            cursor.close();

            if (currentQty >= amount) {
                int newQty = currentQty - amount;
                ContentValues values = new ContentValues();
                values.put(COL_FOOD_QUANTITY, newQty);
                if (newQty == 0) {
                    values.put(COL_FOOD_STATUS, Constants.FOOD_OUT_OF_STOCK);
                }
                db.update(TABLE_FOODS, values, COL_FOOD_ID + "=?", new String[]{foodId});
                return true;
            }
        }
        return false;
    }

    private Food cursorToFood(Cursor cursor) {
        Food food = new Food();
        food.setFoodId(cursor.getString(cursor.getColumnIndexOrThrow(COL_FOOD_ID)));
        food.setPartnerId(cursor.getString(cursor.getColumnIndexOrThrow(COL_FOOD_PARTNER_ID)));
        food.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_FOOD_NAME)));
        food.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COL_FOOD_DESCRIPTION)));
        food.setOriginalPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_FOOD_ORIGINAL_PRICE)));
        food.setDiscountPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_FOOD_DISCOUNT_PRICE)));
        food.setPhotoUrl(cursor.getString(cursor.getColumnIndexOrThrow(COL_FOOD_PHOTO_URL)));
        food.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow(COL_FOOD_QUANTITY)));
        food.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COL_FOOD_STATUS)));
        food.setPartnerName(cursor.getString(cursor.getColumnIndexOrThrow(COL_FOOD_PARTNER_NAME)));
        food.setPartnerAddress(cursor.getString(cursor.getColumnIndexOrThrow(COL_FOOD_ADDRESS)));
        food.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(COL_FOOD_CREATED_AT)));
        return food;
    }

    // --- Booking Methods ---

    public boolean insertBooking(Booking booking) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_BOOKING_ID, UUID.randomUUID().toString());
        values.put(COL_BOOKING_USER_ID, booking.getUserId());
        values.put(COL_BOOKING_FOOD_ID, booking.getFoodId());
        values.put(COL_BOOKING_PARTNER_ID, booking.getPartnerId());
        values.put(COL_BOOKING_PARTNER_NAME, booking.getPartnerName());
        values.put(COL_BOOKING_FOOD_NAME, booking.getFoodName());
        values.put(COL_BOOKING_USER_NAME, booking.getUserName());
        values.put(COL_BOOKING_QUANTITY, booking.getQuantity());
        values.put(COL_BOOKING_STATUS, booking.getStatus());
        values.put(COL_BOOKING_DELIVERY_METHOD, booking.getDeliveryMethod());
        values.put(COL_BOOKING_DELIVERY_ADDRESS, booking.getDeliveryAddress());
        values.put(COL_BOOKING_TIME_SLOT, booking.getScheduledTimeSlot());
        values.put(COL_BOOKING_TIMESTAMP, System.currentTimeMillis());

        long id = db.insert(TABLE_BOOKINGS, null, values);
        db.close();
        return id != -1;
    }

    public List<Booking> getBookingsByUserId(String userId) {
        List<Booking> bookings = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BOOKINGS, null, COL_BOOKING_USER_ID + "=?",
                new String[]{userId}, null, null, COL_BOOKING_TIMESTAMP + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                bookings.add(cursorToBooking(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return bookings;
    }

    public List<Booking> getBookingsByPartnerId(String partnerId) {
        List<Booking> bookings = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BOOKINGS, null, COL_BOOKING_PARTNER_ID + "=?",
                new String[]{partnerId}, null, null, COL_BOOKING_TIMESTAMP + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                bookings.add(cursorToBooking(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return bookings;
    }

    /**
     * Advanced join query to get bookings with extra food context if needed.
     */
    public List<Booking> getBookingsByUserIdWithFood(String userId) {
        // Since we already store foodName and partnerName in TABLE_BOOKINGS, 
        // a simple query is sufficient, but we'll use this method for consistency.
        return getBookingsByUserId(userId);
    }

    public boolean updateBookingStatus(String bookingId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_BOOKING_STATUS, status);
        int rows = db.update(TABLE_BOOKINGS, values, COL_BOOKING_ID + "=?", new String[]{bookingId});
        db.close();
        return rows > 0;
    }

    public int countCompletedBookings(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + COL_BOOKING_QUANTITY + ") FROM " + TABLE_BOOKINGS +
                " WHERE " + COL_BOOKING_USER_ID + "=? AND " + COL_BOOKING_STATUS + "=?",
                new String[]{userId, Constants.BOOKING_COMPLETED});

        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        return count;
    }

    private Booking cursorToBooking(Cursor cursor) {
        Booking booking = new Booking();
        booking.setBookingId(cursor.getString(cursor.getColumnIndexOrThrow(COL_BOOKING_ID)));
        booking.setUserId(cursor.getString(cursor.getColumnIndexOrThrow(COL_BOOKING_USER_ID)));
        booking.setFoodId(cursor.getString(cursor.getColumnIndexOrThrow(COL_BOOKING_FOOD_ID)));
        booking.setPartnerId(cursor.getString(cursor.getColumnIndexOrThrow(COL_BOOKING_PARTNER_ID)));
        booking.setPartnerName(cursor.getString(cursor.getColumnIndexOrThrow(COL_BOOKING_PARTNER_NAME)));
        booking.setFoodName(cursor.getString(cursor.getColumnIndexOrThrow(COL_BOOKING_FOOD_NAME)));
        booking.setUserName(cursor.getString(cursor.getColumnIndexOrThrow(COL_BOOKING_USER_NAME)));
        booking.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow(COL_BOOKING_QUANTITY)));
        booking.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COL_BOOKING_STATUS)));
        booking.setDeliveryMethod(cursor.getString(cursor.getColumnIndexOrThrow(COL_BOOKING_DELIVERY_METHOD)));
        booking.setDeliveryAddress(cursor.getString(cursor.getColumnIndexOrThrow(COL_BOOKING_DELIVERY_ADDRESS)));
        booking.setScheduledTimeSlot(cursor.getString(cursor.getColumnIndexOrThrow(COL_BOOKING_TIME_SLOT)));
        booking.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(COL_BOOKING_TIMESTAMP)));
        return booking;
    }
}
