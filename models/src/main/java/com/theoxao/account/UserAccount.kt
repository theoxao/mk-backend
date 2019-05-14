package com.theoxao.account

import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document

/**
 * Created by theo on 2018/12/3
 */
@Document(collection = "user_account")
class UserAccount {
    var id: ObjectId? = null
    var nickName: String? = null
    var avatarUrl: String? = null
    var openId: String? = null
    var sessionKey: String? = null
    var unionId: String? = null

    constructor()

    constructor(openId: String, sessionKey: String, unionId: String) {
        this.openId = openId
        this.sessionKey = sessionKey
        this.unionId = unionId
    }
}
