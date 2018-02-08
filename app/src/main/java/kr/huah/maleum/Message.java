package kr.huah.maleum;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by banggrae on 2018. 2. 5..
 */

@Entity
public class Message {

    @PrimaryKey
    public int id;

    public boolean apply;

    @Embedded
    public Plant plant;
}
