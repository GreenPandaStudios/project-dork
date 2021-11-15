import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
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
            playerManager.registerSourceManager(new YoutubeAudioSourceManager());
            AudioPlayer player = playerManager.createPlayer();

            // Create an audio source and add it to the audio connection's queue
            AudioSource source = new LavaplayerAudioSource(api, player);
            audioConnection.setAudioSource(source);

            // You can now use the AudioPlayer like you would normally do with Lavaplayer, e.g.,
            playerManager.loadItem("https://www.youtube.com/watch?v=PNaTS4LkbG0&ab_channel=Elysio", new AudioLoadResultHandler() {
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
                    // Notify the user that we've got nothing
                }

                @Override
                public void loadFailed(FriendlyException throwable) {
                    // Notify the user that everything exploded
                }
            });
        }).exceptionally(e -> {
            // Failed to connect to voice channel (no permissions?)
            e.printStackTrace();
            return null;
        });
    }
}
