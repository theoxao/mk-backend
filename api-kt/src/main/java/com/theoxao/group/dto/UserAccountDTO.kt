package com.theoxao.group.dto

import com.theoxao.account.UserAccount
import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter
import org.springframework.beans.BeanUtils

/**
 * Created by theo on 2018/12/3
 */
class UserAccountDTO {
    var id: String? = null
    var nickName: String? = null
    var avatarUrl: String? = null

    constructor()
    constructor(id: String?, nickName: String?, avatarUrl: String?) {
        this.id = id
        this.nickName = nickName
        this.avatarUrl = avatarUrl
    }

    companion object {

        fun fromEntity(entity: UserAccount?): UserAccountDTO {
            if (entity == null) {
                throw RuntimeException("记录不存在")
            }
            val record = UserAccountDTO()
            BeanUtils.copyProperties(entity, record)
            record.id = entity.id!!.toHexString()
            return record
        }
    }

}
