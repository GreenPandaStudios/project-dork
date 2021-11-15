import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.javacord.api.DiscordApi;
import org.javacord.api.audio.AudioSource;
import org.javacord.api.entity.channel.ServerVoiceChannel;

public class AudioManager {
    static void startAudio(DiscordApi api, ServerVoiceChannel validVoiceChannel) {
        validVoiceChannel.connect().thenAccept(audioConnection -> {
            // Create a player manager
            AudioPlayerManager playerManager = new DefaultAudioPlayerManager();

            YoutubeAudioSourceManager youtubeManager = new YoutubeAudioSourceManager();
            LocalAudioSourceManager localManager = new LocalAudioSourceManager();

            AudioPlayer player = playerManager.createPlayer();

            // Create an audio source and add it to the audio connection's queue
            AudioSource source = new LavaplayerAudioSource(api, player);
            audioConnection.setAudioSource(source);


//            playNewSound(youtubeManager, playerManager, player, "https://www.youtube.com/watch?v=PNaTS4LkbG0&ab_channel=Elysio");
//            playNewSound(localManager, playerManager, player, getLocalPath("doorOpen_1.ogg"));

        }).exceptionally(e -> {
            // Failed to connect to voice channel (no permissions?)
            e.printStackTrace();
            return null;
        });
    }

    private static void playNewSound(AudioSourceManager sourceManager, AudioPlayerManager playerManager, AudioPlayer player, String audioName) {
        playerManager.registerSourceManager(sourceManager);

        // Play RPG music from YouTube
        playerManager.loadItem(audioName, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                player.playTrack(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                for (AudioTrack track : playlist.getTracks()) {
                    player.playTrack(track);
                }
            }

            @Override
            public void noMatches() {
                System.out.println("No matches to audio " + audioName);
            }

            @Override
            public void loadFailed(FriendlyException throwable) {
                System.out.println("Everything failed to load for audio " + audioName);
            }
        });

    }

    static String getLocalPath(String toLocalFile) {
        return String.format("src/main/resources/audio/%s", toLocalFile);
    }
}
