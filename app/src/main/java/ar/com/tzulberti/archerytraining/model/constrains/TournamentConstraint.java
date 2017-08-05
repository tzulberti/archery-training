package ar.com.tzulberti.archerytraining.model.constrains;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Has the constrains for the tournament/playoff information
 *
 * Created by tzulberti on 7/19/17.
 */
public class TournamentConstraint implements Serializable {

    public int id;
    public String name;
    public boolean isOutdoor;
    public String stringXMLKey;


    public int roundContraint1Id;
    public Integer roundContraint2Id;
    public Integer roundContraint3Id;
    public Integer roundContraint4Id;
    public Integer roundContraint5Id;
    public Integer roundContraint6Id;

    public String translatedName;
    public List<RoundConstraint> roundConstraintList;

    public TournamentConstraint(int id, String name, boolean isOutdoor, String stringXMLKey,
            int roundContraint1Id, Integer roundContraint2Id, Integer roundContraint3Id,
            Integer roundContraint4Id, Integer roundContraint5Id, Integer roundContraint6Id) {
        this.id = id;
        this.name = name;
        this.isOutdoor = isOutdoor;
        this.stringXMLKey = stringXMLKey;
        this.roundContraint1Id = roundContraint1Id;
        this.roundContraint2Id = roundContraint2Id;
        this.roundContraint3Id = roundContraint3Id;
        this.roundContraint4Id = roundContraint4Id;
        this.roundContraint5Id = roundContraint5Id;
        this.roundContraint6Id = roundContraint6Id;
        this.roundConstraintList = new ArrayList<>();
    }

    /**
     * Gets the constraint for the round index based on the information
     *
     * @param roundIndex the round index which starts in 1
     * @return the RoundConstraint for that index
     */
    public RoundConstraint getContraintForRound(int roundIndex) {
        return this.roundConstraintList.get(roundIndex -1);
    }
}
