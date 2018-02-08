package kr.huah.maleum;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by banggrae on 2018. 2. 5..
 */

@Entity
public class Plant {

    @PrimaryKey
    public int id;

    public String name;

    public String phone;
}
