package com.grousale.grousource.roomdatabase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface RoomDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(ProductsRM productsRM);

    @Update
    void update(ProductsRM productsRM);

    @Delete
    void delete(ProductsRM productsRM);
    @Query("DELETE FROM ProductsRDB")
    void deleteAllData();

    @Query("SELECT * FROM productsRDB WHERE name LIKE '%' || :search_query || '%'")
    Flowable<List<String>> searchResults(String search_query);
}
