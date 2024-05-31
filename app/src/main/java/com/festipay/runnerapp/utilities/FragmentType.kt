package com.festipay.runnerapp.utilities

enum class FragmentType {
    INSTALL,
    DEMOLITION,
    INVENTORY,
    FINAL_INVENTORY,
    PROGRAM,
    INSTALL_COMPANY,
    INSTALL_COMPANY_STATUS,
    INSTALL_COMPANY_COMMENTS,
    INSTALL_COMPANY_COMMENTS_ADD,
    INSTALL_COMPANY_SN,
    INSTALL_COMPANY_SN_ADD,
    INSTALL_COMPANY_GPS,
    DEMOLITION_COMPANY,
    DEMOLITION_COMPANY_STATUS,
    DEMOLITION_COMPANY_COMMENTS,
    DEMOLITION_COMPANY_COMMENTS_ADD,
    DEMOLITION_COMPANY_SN,
    DEMOLITION_COMPANY_SN_ADD,
    DEMOLITION_COMPANY_SN_ADD_INSTANT,
    DEMOLITION_COMPANY_GPS,
    DEMOLITION_COMPANY_CAMERA,
    INVENTORY_ITEM,
    INVENTORY_ITEM_ADD,
    INVENTORY_ITEM_STATUS,
    INVENTORY_ITEM_COMMENTS,
    INVENTORY_ITEM_COMMENTS_ADD,
    INVENTORY_ITEM_SN,
    INVENTORY_ITEM_SN_ADD,
    FINAL_INVENTORY_ITEM,
    FINAL_INVENTORY_ITEM_ADD,
    FINAL_INVENTORY_ITEM_STATUS,
    FINAL_INVENTORY_ITEM_COMMENTS,
    FINAL_INVENTORY_ITEM_COMMENTS_ADD,
    FINAL_INVENTORY_ITEM_SN,
    FINAL_INVENTORY_ITEM_SN_ADD,

}

enum class OperationType {
    STATUS_MODIFY,
    COMMENTS,
    SN_HANDLING,
    GPS,
    CAMERA
}