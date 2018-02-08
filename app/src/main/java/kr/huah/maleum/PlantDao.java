package kr.huah.maleum;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Update;

/**
 * Created by banggrae on 2018. 2. 5..
 */

@Dao
public interface PlantDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertPlants(Plant... plants);

    @Update
    public void updatePlants(Plant... plants);

    @Delete
    public void deletePlants(Plant... plants);
}
