package com.theoxao.group

import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter
import org.springframework.data.mongodb.core.mapping.Document

/**
 * Created by theo on 2018/12/18
 */
@Getter
@Setter
@Document(collection = "user_group_mid")
class UserGroup {
    var userId: String? = null
    var groupId: String? = null
    var owner: Boolean? = null

    constructor()
    constructor(userId: String?, groupId: String?, owner: Boolean?) {
        this.userId = userId
        this.groupId = groupId
        this.owner = owner
    }

}
