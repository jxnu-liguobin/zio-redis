package zio.redis.options

trait Strings {

  sealed trait StrAlgoLCS {
    self =>
    private[redis] final def stringify: String =
      self match {
        case StralgoLCS.Strings => "STRINGS"
        case StralgoLCS.Keys    => "KEYS"
      }
  }

  object StralgoLCS {
    case object Strings extends StrAlgoLCS
    case object Keys    extends StrAlgoLCS
  }

  sealed trait StrAlgoLcsQueryType

  object StrAlgoLcsQueryType {
    case object Len                                                           extends StrAlgoLcsQueryType
    case class Idx(minMatchLength: Int = 1, withMatchLength: Boolean = false) extends StrAlgoLcsQueryType
  }

  sealed trait LcsOutput

  object LcsOutput {
    case class Lcs(lcs: String)                            extends LcsOutput
    case class Length(length: Long)                        extends LcsOutput
    case class Matches(matches: List[Match], length: Long) extends LcsOutput
  }

  case class MatchIdx(start: Long, end: Long)
  case class Match(matchIdxA: MatchIdx, matchIdxB: MatchIdx, matchLength: Option[Long] = None)

  sealed trait BitFieldCommand

  object BitFieldCommand {
    sealed case class BitFieldGet(`type`: BitFieldType, offset: Int) extends BitFieldCommand

    sealed case class BitFieldSet(`type`: BitFieldType, offset: Int, value: Long) extends BitFieldCommand

    sealed case class BitFieldIncr(`type`: BitFieldType, offset: Int, increment: Long) extends BitFieldCommand

    sealed trait BitFieldOverflow extends BitFieldCommand { self =>
      private[redis] final def stringify: String =
        self match {
          case BitFieldOverflow.Fail => "FAIL"
          case BitFieldOverflow.Sat  => "SAT"
          case BitFieldOverflow.Wrap => "WRAP"
        }
    }

    object BitFieldOverflow {
      case object Fail extends BitFieldOverflow
      case object Sat  extends BitFieldOverflow
      case object Wrap extends BitFieldOverflow
    }
  }

  sealed trait BitFieldType { self =>
    private[redis] final def stringify: String =
      self match {
        case BitFieldType.UnsignedInt(size) => s"u$size"
        case BitFieldType.SignedInt(size)   => s"i$size"
      }
  }

  object BitFieldType {
    sealed case class UnsignedInt(size: Int) extends BitFieldType
    sealed case class SignedInt(size: Int)   extends BitFieldType
  }

  sealed trait BitOperation { self =>
    private[redis] final def stringify: String =
      self match {
        case BitOperation.AND => "AND"
        case BitOperation.OR  => "OR"
        case BitOperation.XOR => "XOR"
        case BitOperation.NOT => "NOT"
      }
  }

  object BitOperation {
    case object AND extends BitOperation
    case object OR  extends BitOperation
    case object XOR extends BitOperation
    case object NOT extends BitOperation
  }

  sealed case class BitPosRange(start: Long, end: Option[Long])

  case object KeepTtl {
    private[redis] def stringify: String = "KEEPTTL"
  }

  type KeepTtl = KeepTtl.type
}
