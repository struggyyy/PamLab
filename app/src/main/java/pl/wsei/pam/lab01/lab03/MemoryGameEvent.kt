package pl.wsei.pam.lab01.lab03

data class MemoryGameEvent(
    val tiles: List<Tile>,
    val state: GameStates
)
