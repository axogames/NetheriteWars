package dev.dercoderjo.netheritewars.common

import dev.dercoderjo.netheritewars.NetheriteWars
import java.sql.DriverManager
import java.sql.Timestamp

class Database(val plugin: NetheriteWars) {
    val connection = DriverManager.getConnection(plugin.CONFIG.getString("DATABASE.URL"), plugin.CONFIG.getString("DATABASE.USER"), plugin.CONFIG.getString("DATABASE.PASSWORD"))
    val statement = connection.createStatement()

    fun getPlayer(uuid: String): Player {
        checkConnection()

        val query = "SELECT * FROM players WHERE uuid = ?"
        val pstmt = connection.prepareStatement(query)
        pstmt.setString(1, uuid)

        val playerSet = pstmt.executeQuery()

        return if (playerSet.next()) {
            Player(
                playerSet.getString("uuid"),
                playerSet.getInt("netherite"),
                playerSet.getInt("deaths"),
                Position.valueOf(playerSet.getString("position")),
                Teams.valueOf(playerSet.getString("team")),
                playerSet.getBoolean("whitelisted"),
                playerSet.getBoolean("orga")
            )
        } else {
            Player(uuid, 0, 0, Position.BORDER, Teams.UNSET, false, false)
        }
    }

    fun setPlayer(playerData: Player) {
        checkConnection()

        val query = if (checkPlayer(playerData.uuid)) {
            "UPDATE players SET netherite = ?, deaths = ?, position = ?, team = ?, whitelisted = ?, orga = ? WHERE uuid = ?"
        } else {
            "INSERT INTO players (uuid, netherite, deaths, position, team, whitelisted, orga) VALUES (?, ?, ?, ?, ?, ?, ?)"
        }

        val pstmt = connection.prepareStatement(query)

        if (checkPlayer(playerData.uuid)) {
            pstmt.setInt(1, playerData.netherite)
            pstmt.setInt(2, playerData.deaths)
            pstmt.setString(3, playerData.position.name)
            pstmt.setString(4, playerData.team.name)
            pstmt.setBoolean(5, playerData.whitelisted)
            pstmt.setBoolean(6, playerData.orga)
            pstmt.setString(7, playerData.uuid)
        } else {
            pstmt.setString(1, playerData.uuid)
            pstmt.setInt(2, playerData.netherite)
            pstmt.setInt(3, playerData.deaths)
            pstmt.setString(4, playerData.position.name)
            pstmt.setString(5, playerData.team.name)
            pstmt.setBoolean(6, playerData.whitelisted)
            pstmt.setBoolean(7, playerData.orga)
        }

        pstmt.executeUpdate()
    }

    fun checkPlayer(uuid: String): Boolean {
        checkConnection()

        val playerSet = statement.executeQuery("SELECT * FROM players WHERE uuid = '$uuid'")
        return playerSet.next()
    }

    fun disconnect() {
        connection.close()
    }

    fun getTeam(team: Teams): Team {
        checkConnection()

        val teamSet = statement.executeQuery("SELECT * FROM teams WHERE color = '${team.name}'")
        teamSet.next()
        return Team(Teams.valueOf(teamSet.getString("color")), teamSet.getInt("netherite"))
    }

    fun setTeam(team: Team) {
        checkConnection()

        statement.executeUpdate("UPDATE teams SET netherite = ${team.netherite} WHERE color = '${team.team}'")
    }

    fun setDeaths(player: Player) {
        checkConnection()

        statement.executeUpdate("UPDATE players SET deaths = ${player.deaths} WHERE uuid = '${player.uuid}'")
    }

    fun getBattleRoyal(): BattleRoyal {
        checkConnection()

        val stmt = connection.prepareStatement("SELECT * FROM battle_royal WHERE status != 'ENDED' ORDER BY ends_at DESC LIMIT 1")
        val resultSet = stmt.executeQuery()
        return if (resultSet.next()) {
            BattleRoyal(BattleRoyalStatus.valueOf(resultSet.getString("status")), resultSet.getTimestamp("ends_at")?.time, resultSet.getTimestamp("paused_at")?.time)
        } else {
            BattleRoyal(null, null, null)
        }
    }

    fun setBattleRoyal(battleRoyal: BattleRoyal) {
        checkConnection()

        val stmt = connection.prepareStatement("SELECT * FROM battle_royal WHERE status != 'ENDED'")
        val resultSet = stmt.executeQuery()
        if (resultSet.next()) {
            val updateStmt = connection.prepareStatement("UPDATE battle_royal SET status = ?, ends_at = ?, paused_at = ? WHERE status != 'ENDED'")
            updateStmt.setString(1, battleRoyal.status?.name)
            if (battleRoyal.endsAt != null) {
                updateStmt.setTimestamp(2, Timestamp(battleRoyal.endsAt))
            } else {
                updateStmt.setNull(2, java.sql.Types.BIGINT)
            }
            if (battleRoyal.pausedAt != null) {
                updateStmt.setTimestamp(3, Timestamp(battleRoyal.pausedAt))
            } else {
                updateStmt.setNull(3, java.sql.Types.BIGINT)
            }
            updateStmt.executeUpdate()
        } else {
            val insertStmt = connection.prepareStatement("INSERT INTO battle_royal (status, ends_at, paused_at) VALUES (?, ?, ?)")
            insertStmt.setString(1, battleRoyal.status?.name)
            if (battleRoyal.endsAt != null) {
                insertStmt.setTimestamp(2, Timestamp(battleRoyal.endsAt))
            } else {
                insertStmt.setNull(2, java.sql.Types.BIGINT)
            }
            if (battleRoyal.pausedAt != null) {
                insertStmt.setTimestamp(3, Timestamp(battleRoyal.pausedAt))
            } else {
                insertStmt.setNull(3, java.sql.Types.BIGINT)
            }
            insertStmt.executeUpdate()
        }
    }

    private fun checkConnection() {
        try {
            connection.isValid(5)
        } catch (e: Exception) {
            plugin.DATABASE = Database(plugin)
        }
    }
}

enum class Position {
    BLUE,
    RED,
    BORDER
}
