import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.javacord.api.DiscordApi;
import org.javacord.api.audio.AudioSource;
import org.javacord.api.entity.channel.ServerVoiceChannel;

enum AudioSourceType {
    LOCAL, YOUTUBE
}

public class AudioManager {
    static YoutubeAudioSourceManager youtubeManager;
    static LocalAudioSourceManager localManager;
    static AudioPlayerManager playerManager;
    static AudioPlayer player;

    static void startAudio(DiscordApi api, ServerVoiceChannel validVoiceChannel) {
        validVoiceChannel.connect().thenAccept(audioConnection -> {
            // Create a player manager
            playerManager = new DefaultAudioPlayerManager();

            youtubeManager = new YoutubeAudioSourceManager();
            localManager = new LocalAudioSourceManager();

            player = playerManager.createPlayer();

            // Create an audio source and add it to the audio connection's queue
            AudioSource source = new LavaplayerAudioSource(api, player);
            audioConnection.setAudioSource(source);

            playNewSound(AudioSourceType.YOUTUBE, "https://www.youtube.com/watch?v=PNaTS4LkbG0&ab_channel=Elysio");

        }).exceptionally(e -> {
            // Failed to connect to voice channel (no permissions?)
            e.printStackTrace();
            return null;
        });
    }

    static void playNewSound(AudioSourceType sourceType, String audioName) {
        if (sourceType == AudioSourceType.LOCAL) {
            playerManager.registerSourceManager(localManager);
        } else if (sourceType == AudioSourceType.YOUTUBE) {
            playerManager.registerSourceManager(youtubeManager);
        } else {
            return;
        }

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
