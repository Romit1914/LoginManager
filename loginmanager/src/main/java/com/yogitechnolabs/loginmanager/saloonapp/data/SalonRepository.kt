package com.yogitechnolabs.loginmanager.saloonapp.data

class SalonRepository {

    fun getOwnerData(ownerId: String): Owner {

        return Owner(
            id = ownerId,
            employees = listOf(

                Employee(
                    id = "emp1",
                    name = "Rahul",
                    profileUrl = null,
                    services = listOf(
                        Service("Hair Cut", 120),
                        Service("Beard", 80)
                    )
                ),

                Employee(
                    id = "emp2",
                    name = "Suresh",
                    profileUrl = null,
                    services = listOf(
                        Service("Hair Color", 300)
                    )
                ),

                Employee(
                    id = "emp3",
                    name = "Amit",
                    profileUrl = null,
                    services = emptyList()
                )
            )
        )
    }
}