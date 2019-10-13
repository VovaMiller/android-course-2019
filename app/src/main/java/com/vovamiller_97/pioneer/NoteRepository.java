package com.vovamiller_97.pioneer;

import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NoteRepository {

    private static final Map<String, Note> NOTE_LIST = new LinkedHashMap<>();

    public static void initialize(final Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2011, 6, 12);
        final Date date = calendar.getTime();

        int titlesIds[] = new int[] {
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
        int textsIds[] = new int[] {
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
        int drawableIds[] = new int[] {
                R.drawable.nodata,
                R.drawable.mutant_bloodsucker,
                R.drawable.mutant_boar,
                R.drawable.mutant_burer,
                R.drawable.mutant_cat,
                R.drawable.mutant_chimera,
                R.drawable.mutant_controller,
                R.drawable.mutant_dog,
                R.drawable.mutant_flesh,
                R.drawable.mutant_gigant,
                R.drawable.mutant_izlom,
                R.drawable.mutant_poltergeist,
                R.drawable.mutant_pseudodog,
                R.drawable.mutant_rat,
                R.drawable.mutant_snork,
                R.drawable.mutant_tushkano,
                R.drawable.mutant_zombie
        };

        for (int i = 0; i < 17; ++i) {
            String id = "id" + i;
            NOTE_LIST.put(id, new Note(id,
                    context.getString(titlesIds[i]),
                    context.getString(textsIds[i]),
                    date,
                    drawableIds[i]));

        }
    }

    public static List<Note> getNoteList() {
        return new ArrayList<>(NOTE_LIST.values());
    }

    public static Note getNoteById(final String id) {
        return NOTE_LIST.get(id);
    }

}
