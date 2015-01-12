package salve.resource

case class PlayerResource(
  id: Int,
  playerName: String,
  hero: String,
  kills: Int,
  deaths: Int,
  assists: Int,
  reliableGold: Int,
  unreliableGold: Int,
  totalGold: Int,
  level: Int,
  lastHits: Int,
  denies: Int
)
