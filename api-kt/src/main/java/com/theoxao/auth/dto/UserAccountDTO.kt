package com.theoxao.auth.dto

import com.theoxao.account.UserAccount
import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter

import java.util.UUID

/**
 * Created by theo on 2018/12/3
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class UserAccountDTO {
    var token: String? = null
    var id: String? = null
    var nickName: String? = null
    var avatarUrl: String? = null

    companion object {

        fun fromEntity(entity: UserAccount): UserAccountDTO {
            val record = UserAccountDTO()
            record.id = entity.id!!.toHexString()
            record.avatarUrl = entity.avatarUrl
            record.nickName = entity.nickName
            return record
        }
    }

}
