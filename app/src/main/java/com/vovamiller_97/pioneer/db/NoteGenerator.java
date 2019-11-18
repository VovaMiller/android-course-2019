package com.vovamiller_97.pioneer.db;

import android.content.Context;

import com.vovamiller_97.pioneer.R;

import java.util.Random;
//import java.util.concurrent.ThreadLocalRandom;
import java.util.Date;

/**
 * Temporary class for creating notes with random content (title & text).
 */
public class NoteGenerator {

    private static final Random RANDOMIZER = new Random(System.currentTimeMillis());

    private static final int SET_LENGTH = 17;

    private static final int[] IDS_TITLE = new int[] {
            R.string.mutant_crow,
            R.string.mutant_bloodsucker,
            R.string.mutant_boar,
            R.string.mutant_burer,
            R.string.mutant_cat,
            R.string.mutant_chimera,
            R.string.mutant_controller,
            R.string.mutant_dog,
            R.string.mutant_flesh,
            R.string.mutant_gigant,
            R.string.mutant_izlom,
            R.string.mutant_poltergeist,
            R.string.mutant_pseudodog,
            R.string.mutant_rat,
            R.string.mutant_snork,
            R.string.mutant_tushkano,
            R.string.mutant_zombie
    };
    private static final int[] IDS_TEXT = new int[] {
            R.string.mutant_crow_info,
            R.string.mutant_bloodsucker_info,
            R.string.mutant_boar_info,
            R.string.mutant_burer_info,
            R.string.mutant_cat_info,
            R.string.mutant_chimera_info,
            R.string.mutant_controller_info,
            R.string.mutant_dog_info,
            R.string.mutant_flesh_info,
            R.string.mutant_gigant_info,
            R.string.mutant_izlom_info,
            R.string.mutant_poltergeist_info,
            R.string.mutant_pseudodog_info,
            R.string.mutant_rat_info,
            R.string.mutant_snork_info,
            R.string.mutant_tushkano_info,
            R.string.mutant_zombie_info
    };

    private NoteGenerator() {}

    public static Note random(Context context, Date date, String image) {
        Note note = new Note();
        int r = RANDOMIZER.nextInt(SET_LENGTH);
//        int r = ThreadLocalRandom.current().nextInt(SET_LENGTH);
        if (context != null) {
            note.setText(context.getString(IDS_TEXT[r]));
            note.setTitle(context.getString(IDS_TITLE[r]));
        }
        note.setImage(image);
        note.setDate(date);
        return note;
    }

}