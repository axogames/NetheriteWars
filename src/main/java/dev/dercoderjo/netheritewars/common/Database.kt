package dev.dercoderjo.netheritewars.common

import dev.dercoderjo.netheritewars.NetheriteWars
import java.sql.DriverManager

class Database(plugin: NetheriteWars) {
    val connection = DriverManager.getConnection(plugin.CONFIG.getString("DATABASE.URL"), plugin.CONFIG.getString("DATABASE.USER"), plugin.CONFIG.getString("DATABASE.PASSWORD"))
    val statement = connection.createStatement()

    fun getPlayer(uuid: String): Player {
        val playerSet = statement.executeQuery("SELECT * FROM players WHERE uuid = '$uuid'")
        return if (playerSet.next()) {
            Player(playerSet.getString("uuid"), playerSet.getInt("netherite"), playerSet.getInt("deaths"), Position.valueOf(playerSet.getString("position")), Teams.valueOf(playerSet.getString("team")), playerSet.getBoolean("whitelisted"))
        } else {
            Player(uuid, 0, 0, Position.BORDER, Teams.UNSET, false)
        }
    }

    fun setPlayer(playerData: Player) {
        if (checkPlayer(playerData.uuid)) {
            statement.executeUpdate("UPDATE players SET netherite = ${playerData.netherite}, deaths = ${playerData.deaths}, position = '${playerData.position}', team = '${playerData.team}', whitelisted = '${if (playerData.whitelisted) 1 else 0}' WHERE uuid = '${playerData.uuid}'")
            return
        } else {
            statement.executeUpdate("INSERT INTO players (uuid, netherite, deaths, position, team, whitelisted) VALUES ('${playerData.uuid}', ${playerData.netherite}, ${playerData.deaths}, '${playerData.position}', '${playerData.team}', '${if (playerData.whitelisted) 1 else 0}')")
        }
    }

    fun checkPlayer(uuid: String): Boolean {
        val playerSet = statement.executeQuery("SELECT * FROM players WHERE uuid = '$uuid'")
        return playerSet.next()
    }

    fun disconnect() {
        connection.close()
    }

    fun getTeam(team: Teams): Team {
        println(team.name)
        val teamSet = statement.executeQuery("SELECT * FROM teams WHERE color = '${team.name}'")
        teamSet.next()
        return Team(Teams.valueOf(teamSet.getString("color")), teamSet.getInt("netherite"))
    }

    fun setTeam(team: Team) {
        println(team.netherite)
        statement.executeUpdate("UPDATE teams SET netherite = ${team.netherite} WHERE color = '${team.team}'")
    }
}


class Player(val uuid: String, val netherite: Int, val deaths: Int, var position: Position, val team: Teams, val whitelisted: Boolean, val orga: Boolean = false) {
}

class Team(val team: Teams, var netherite: Int) {
}

enum class Position {
    BLUE,
    RED,
    BORDER
}

enum class Teams {
    BLUE,
    RED,
    UNSET
}
