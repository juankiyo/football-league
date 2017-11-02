package com.juan.nunez.application_service;

import com.juan.nunez.dto.LeagueTableEntry;
import com.juan.nunez.dto.Match;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.juan.nunez.constants.LeagueTableConstants.SCORE_TYPE_DRAW;
import static com.juan.nunez.constants.LeagueTableConstants.SCORE_TYPE_LOSE;
import static com.juan.nunez.constants.LeagueTableConstants.SCORE_TYPE_WIN;

public class LeagueTable {


    private ArrayList<LeagueTableEntry> leagueTableEntries;
    private Map<String, LeagueTableEntry> leagueTableEntryMap;

    public LeagueTable(final List<Match> matches) {
        if (leagueTableEntries != null && !leagueTableEntries.isEmpty()) {
            leagueTableEntryMap = leagueTableEntries.stream()
                    .collect(Collectors.toMap(LeagueTableEntry::getTeamName, Function.identity()));
        } else {
            leagueTableEntryMap = new HashMap<>();
        }
        matches.forEach(this::updateLeagueTableMap);
        leagueTableEntries = new ArrayList<>(leagueTableEntryMap.values());
    }

    /**
     * Get the ordered list of league table entries for this league table.
     *
     * @return
     */
    public List<LeagueTableEntry> getTableEntries() {
        return leagueTableEntries.stream().sorted().collect(Collectors.toList());
    }

    private void updateLeagueTableMap(Match match) {
        int homePoints = isADraw(match) ? SCORE_TYPE_DRAW : isHomeWinner(match) ? SCORE_TYPE_WIN : SCORE_TYPE_LOSE;
        int awayPoints = isADraw(match) ? SCORE_TYPE_DRAW : isHomeWinner(match) ? SCORE_TYPE_LOSE : SCORE_TYPE_WIN;
        addLeagueTableEntry(match.getHomeTeam(), homePoints, match.getHomeScore(), match.getAwayScore());
        addLeagueTableEntry(match.getAwayTeam(), awayPoints, match.getAwayScore(), match.getHomeScore());
    }

    private void addLeagueTableEntry(String teamName, int points, int goalsFor, int goalsAgainst) {
        LeagueTableEntry leagueTableEntry;
        if (leagueTableEntryMap.containsKey(teamName)) {
            leagueTableEntry = leagueTableEntryMap.get(teamName);
            int totalGoalsFor = leagueTableEntry.getGoalsFor() + goalsFor;
            int totalGoalsAgainst = leagueTableEntry.getGoalsAgainst() + goalsAgainst;
            leagueTableEntry.setPlayed(leagueTableEntry.getPlayed() + 1);
            leagueTableEntry.setPoints(leagueTableEntry.getPoints() + points);
            leagueTableEntry.setGoalsFor(totalGoalsFor);
            leagueTableEntry.setGoalsAgainst(totalGoalsAgainst);
            leagueTableEntry.setGoalDifference(totalGoalsFor - totalGoalsAgainst);
        } else {
            leagueTableEntry = new LeagueTableEntry(teamName, 1, 0, 0, 0, goalsFor, goalsAgainst,
                    goalsFor - goalsAgainst, points);
        }

        switch (points) {
            case SCORE_TYPE_WIN:
                leagueTableEntry.setWon(leagueTableEntry.getWon() + 1);
                break;
            case SCORE_TYPE_LOSE:
                leagueTableEntry.setLost(leagueTableEntry.getLost() + 1);
                break;
            case SCORE_TYPE_DRAW:
                leagueTableEntry.setDrawn(leagueTableEntry.getDrawn() + 1);
                break;
        }
        leagueTableEntryMap.put(teamName, leagueTableEntry);

    }

    private boolean isADraw(Match match) {
        return match.getHomeScore() == match.getAwayScore();
    }

    private boolean isHomeWinner(Match match) {
        return match.getHomeScore() > match.getAwayScore();
    }
}
