/**
 * Transform order data from API format to UI-expected format
 * This handles the mismatch between backend OrderDTO and frontend expectations
 */
export const transformOrderData = (apiOrderData) => {
    if (!apiOrderData) return null;

    // Handle both API format and mock data format
    return {
        // Order identification
        orderID: apiOrderData.id || apiOrderData.orderID,
        id: apiOrderData.id || apiOrderData.orderID,
        
        // Order metadata
        status: apiOrderData.status,
        orderDate: apiOrderData.orderDate || apiOrderData.createdAt || new Date().toISOString(),
        
        // Pricing
        totalAmount: apiOrderData.totalPrice || apiOrderData.totalAmount || 0,
        totalPrice: apiOrderData.totalPrice || apiOrderData.totalAmount || 0,
        
        // Customer information (from deliveryInfo or direct fields)
        customerName: apiOrderData.deliveryInfo?.recipientName || apiOrderData.customerName,
        customerPhone: apiOrderData.deliveryInfo?.phoneNumber || apiOrderData.customerPhone,
        customerEmail: apiOrderData.deliveryInfo?.mail || apiOrderData.customerEmail,
        
        // Delivery information
        deliveryInfo: {
            recipientName: apiOrderData.deliveryInfo?.recipientName || apiOrderData.customerName,
            phoneNumber: apiOrderData.deliveryInfo?.phoneNumber || apiOrderData.customerPhone,
            mail: apiOrderData.deliveryInfo?.mail || apiOrderData.customerEmail,
            addressDetail: apiOrderData.deliveryInfo?.addressDetail || apiOrderData.shippingAddress,
            city: apiOrderData.deliveryInfo?.city,
            district: apiOrderData.deliveryInfo?.district,
            isRushOrder: apiOrderData.deliveryInfo?.isRushOrder || apiOrderData.isRushOrder
                  },
          
         // Order items (normalize field names)
         items: (apiOrderData.items || []).map(item => ({
             productID: item.productID,
             title: item.productTitle || item.title,
             productTitle: item.productTitle || item.title,
             price: item.productPrice || item.price,
             productPrice: item.productPrice || item.price,
             quantity: item.quantity,
             category: item.category,
             imageURL: item.imageURL,
             description: item.description
         })),
          
          // Additional fields
        paymentMethod: apiOrderData.paymentMethod || "Cash on Delivery",
        isRushOrder: apiOrderData.deliveryInfo?.isRushOrder || apiOrderData.isRushOrder || false,
        notes: apiOrderData.notes,
        
        // Keep original data for debugging
        _originalData: apiOrderData
    };
};

/**
 * Transform array of orders
 */
export const transformOrdersArray = (ordersArray) => {
    if (!Array.isArray(ordersArray)) return [];
    return ordersArray.map(transformOrderData);
}; 