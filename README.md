# Scrapguard Base

This is the base of an anticheat I made as a personal project. I had fun coding it and thought some other people may want to try out designing their own checks. The permission to see alerts is `scrapguard.alerts'.

## Dependencies
This plugin requires [packetevents](https://github.com/retrooper/packetevents/) to be installed as a plugin on your server.

## Some info on the sniff command
Scrapguard comes with a packet sniffer for looking at packet data as they are sent. The base command is `/sniff`, with the parameters being as follows:
- `whitelist`: A boolean, where true means that all packets of `packets` are to be listened for and where false means that all packets not in `packets` are listened to.
- `packets`: a vararg of packets (autocompleted) along with any extra data requested.

### Extra Data
Say you want to listen for the PLAYER_POSITION_AND_ROTATION packet, but you also want to view the player's ping and their jerkY value from the kinematics data. You would use the following packet entry:
```
PLAYER_POSITION_AND_ROTATION(ping|kinematics.jerkY)
```

All packet data is included by default. All data is shown when hovering the sniff message
