package com.sxmt.metadata;

import com.sxmt.config.Properties;
import com.sxmt.connection.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by douglas.calderon on 7/5/2015.
 */
public class ArtistGenreStorer
{
    public static int storeArtistAndGenres(String title, String artist) throws SQLException
    {
        List<String> genres = EchonestFetcher.getGenres(title, artist);

        String genresPlaceHolder = "";
        for(String genre: genres){
            genresPlaceHolder += "?,";
        }
        if(genresPlaceHolder.length() > 0){
            genresPlaceHolder = genresPlaceHolder.substring(0,genresPlaceHolder.length() -1 );
        }

        final String DBNAME = Properties.getInstance().getAppDatabaseName();

        final String artistInsert =
                "INSERT IGNORE INTO " + DBNAME + "." + TableNames.ARTISTS +
                "   ("+ ArtistsFields.ARTIST_NAME +") VALUES (?);";

        final String artistSelect =
                "SELECT "+ ArtistsFields.ARTIST_ID +" FROM " + DBNAME + "." + TableNames.ARTISTS +
                "   WHERE "+ ArtistsFields.ARTIST_NAME +" = ?";

        final String genresInsert =
                "INSERT IGNORE INTO "+ DBNAME + "." + TableNames.GENRES +
                "   ("+ GenresFields.GENRE +") VALUES (?);";

        final String genresSelect =
                "SELECT "+ GenresFields.GENRE_ID +" FROM " + DBNAME + "." + TableNames.GENRES +
                "   WHERE "+GenresFields.GENRE+" IN ("+genresPlaceHolder+")";

        final String artistGenresInsert =
                "INSERT IGNORE INTO "+ DBNAME + "." + TableNames.ARTIST_GENRES +
                "   ("+ ArtistGenresFields.ARTIST_ID+", "+ ArtistGenresFields.GENRE_ID +") VALUES(?,?)";

        int artistId;

        try(final Connection connection = SQLConnectionFactory.newMySQLConnection();
            final PreparedStatement artistInsertStatement = connection.prepareStatement(artistInsert);
            final PreparedStatement artistSelectStatement = connection.prepareStatement(artistSelect)
        ){
            artistInsertStatement.setString(1, artist);
            artistInsertStatement.execute();

            artistSelectStatement.setString(1, artist);
            try (final ResultSet results = artistSelectStatement.executeQuery())
            {
                if(results.next()){
                    artistId = results.getInt(ArtistsFields.ARTIST_ID);
                }else{
                    throw new IllegalStateException("I JUST ADDEDED IT, BUT IS NO DERE");
                }
            }
        }

        if(genres.size() > 0){

            try (final Connection connection = SQLConnectionFactory.newMySQLConnection();
                 final PreparedStatement genresInsertStatement = connection.prepareStatement(genresInsert);
                 final PreparedStatement genresSelectStatement = connection.prepareStatement(genresSelect);
                 final PreparedStatement artistGenresInsertStatement = connection.prepareStatement(artistGenresInsert)
            ){



                for(String genre : genres){
                    genresInsertStatement.setString(1, genre);
                    genresInsertStatement.execute();
                }

                int count = 1;
                for(String genre : genres){
                    genresSelectStatement.setString(count++, genre);
                }
                List<Integer> genreIds = new LinkedList<>();
                try (final ResultSet results = genresSelectStatement.executeQuery())
                {
                    while(results.next()){
                        genreIds.add(results.getInt(GenresFields.GENRE_ID));
                    }
                }

                for(Integer genreId : genreIds){
                    artistGenresInsertStatement.setInt(1, artistId);
                    artistGenresInsertStatement.setInt(2, genreId);
                    artistGenresInsertStatement.execute();
                }
            }
        }
        return artistId;
    }
}
