package io.heimui.demo.screens

object DemoScreenCatalog {

    val ECOMMERCE_SCREEN = """
    {
      "id": "ecommerce_home",
      "version": "1.0.0",
      "title": "Store & Deals",
      "applySafeInsets": true,
      "root": {
        "type": "lazy_column",
        "id": "main_list",
        "spacing": 16,
        "padding": 16,
        "items": [
          {
            "type": "card",
            "id": "hero_banner",
            "backgroundColor": "#161D2F",
            "cornerRadius": 16,
            "padding": 20,
            "actions": [
              {
                "type": "show_snackbar",
                "message": "🔥 Summer Sale Campaign activated!"
              }
            ],
            "child": {
              "type": "container",
              "id": "hero_content",
              "direction": "VERTICAL",
              "spacing": 10,
              "children": [
                {
                  "type": "badge",
                  "id": "hero_tag",
                  "text": "⚡ FLASH SALE · 50% OFF",
                  "backgroundColor": "#00E5FF",
                  "textColor": "#0B0F19"
                },
                {
                  "type": "text",
                  "id": "hero_title",
                  "text": "Cyberpunk Gear & Tech",
                  "style": "TITLE_LARGE",
                  "color": "#FFFFFF"
                },
                {
                  "type": "text",
                  "id": "hero_sub",
                  "text": "Dynamic layout streamed from HeimUI backend without app store releases.",
                  "style": "BODY_MEDIUM",
                  "color": "#94A3B8"
                }
              ]
            }
          },
          {
            "type": "text",
            "id": "cat_header",
            "text": "Popular Categories",
            "style": "TITLE_MEDIUM",
            "color": "#00E5FF"
          },
          {
            "type": "lazy_row",
            "id": "categories_row",
            "spacing": 10,
            "items": [
              {
                "type": "badge",
                "id": "cat_1",
                "text": "🎧 Audio Gear",
                "backgroundColor": "#4F46E5",
                "textColor": "#FFFFFF"
              },
              {
                "type": "badge",
                "id": "cat_2",
                "text": "⌚ Smartwatches",
                "backgroundColor": "#1E293B",
                "textColor": "#E2E8F0"
              },
              {
                "type": "badge",
                "id": "cat_3",
                "text": "💻 Keyboards",
                "backgroundColor": "#1E293B",
                "textColor": "#E2E8F0"
              },
              {
                "type": "badge",
                "id": "cat_4",
                "text": "📱 Mobile",
                "backgroundColor": "#1E293B",
                "textColor": "#E2E8F0"
              }
            ]
          },
          {
            "type": "text",
            "id": "featured_header",
            "text": "Featured Products",
            "style": "TITLE_MEDIUM",
            "color": "#FFFFFF"
          },
          {
            "type": "card",
            "id": "product_card_1",
            "backgroundColor": "#1E293B",
            "cornerRadius": 12,
            "padding": 16,
            "actions": [
              {
                "type": "show_bottom_sheet",
                "title": "Product Details",
                "is_dismissible": true,
                "content": {
                  "type": "container",
                  "id": "sheet_body",
                  "direction": "VERTICAL",
                  "spacing": 12,
                  "padding": 20,
                  "children": [
                    {
                      "type": "text",
                      "id": "sheet_title",
                      "text": "Heimdall Cyber Pro Headset",
                      "style": "TITLE_LARGE"
                    },
                    {
                      "type": "text",
                      "id": "sheet_desc",
                      "text": "Active Noise Cancelling with low-latency Bluetooth 5.4.",
                      "style": "BODY_MEDIUM"
                    },
                    {
                      "type": "button",
                      "id": "sheet_buy_btn",
                      "title": "Confirm Order ($299)",
                      "variant": "FILLED",
                      "is_full_width": true,
                      "actions": [
                        { "type": "dismiss_modal" },
                        { "type": "show_snackbar", "message": "✅ Added to Cart!" }
                      ]
                    }
                  ]
                }
              }
            ],
            "child": {
              "type": "container",
              "id": "prod_row",
              "direction": "HORIZONTAL",
              "spacing": 14,
              "alignment": "CENTER",
              "children": [
                {
                  "type": "icon",
                  "id": "prod_icon",
                  "name": "shopping_cart",
                  "tint": "#00E5FF",
                  "size": 36
                },
                {
                  "type": "container",
                  "id": "prod_info",
                  "direction": "VERTICAL",
                  "spacing": 4,
                  "children": [
                    {
                      "type": "text",
                      "id": "prod_name",
                      "text": "Heimdall Cyber Pro Headset",
                      "style": "BODY_LARGE",
                      "color": "#FFFFFF"
                    },
                    {
                      "type": "text",
                      "id": "prod_price",
                      "text": "$299.00 USD",
                      "style": "TITLE_SMALL",
                      "color": "#00E5FF"
                    }
                  ]
                }
              ]
            }
          }
        ]
      }
    }
    """.trimIndent()

    val FINTECH_KYC_SCREEN = """
    {
      "id": "fintech_kyc",
      "version": "1.0.0",
      "title": "KYC Account Verification",
      "applySafeInsets": true,
      "root": {
        "type": "lazy_column",
        "id": "kyc_column",
        "spacing": 16,
        "padding": 20,
        "items": [
          {
            "type": "card",
            "id": "kyc_header_card",
            "backgroundColor": "#161D2F",
            "cornerRadius": 12,
            "padding": 16,
            "child": {
              "type": "container",
              "id": "kyc_hdr_content",
              "direction": "VERTICAL",
              "spacing": 6,
              "children": [
                {
                  "type": "badge",
                  "id": "kyc_status",
                  "text": "BANKING TIER 1",
                  "backgroundColor": "#4F46E5",
                  "textColor": "#FFFFFF"
                },
                {
                  "type": "text",
                  "id": "kyc_title",
                  "text": "Identity Verification",
                  "style": "TITLE_MEDIUM",
                  "color": "#FFFFFF"
                },
                {
                  "type": "text",
                  "id": "kyc_desc",
                  "text": "Fields update and validate dynamically according to account classification.",
                  "style": "BODY_SMALL",
                  "color": "#94A3B8"
                }
              ]
            }
          },
          {
            "type": "switch",
            "id": "is_business_switch",
            "state_key": "is_business",
            "label": "Registering as a Corporate Business Account?",
            "initial_checked": false
          },
          {
            "type": "text_field",
            "id": "full_name_input",
            "state_key": "full_name",
            "label": "Full Legal Name",
            "placeholder": "e.g. Julian Velandia",
            "input_type": "TEXT",
            "validation_rules": [
              {
                "type": "REQUIRED",
                "error_message": "Legal name is strictly required"
              }
            ]
          },
          {
            "type": "text_field",
            "id": "email_input",
            "state_key": "email",
            "label": "Verified Work Email",
            "placeholder": "contact@heimui.io",
            "input_type": "EMAIL",
            "validation_rules": [
              {
                "type": "REQUIRED",
                "error_message": "Email is required"
              },
              {
                "type": "EMAIL",
                "error_message": "Please enter a valid email format"
              }
            ]
          },
          {
            "type": "text_field",
            "id": "tax_id_input",
            "state_key": "tax_id",
            "label": "Corporate Tax ID / NIT",
            "placeholder": "900.123.456-7",
            "input_type": "TEXT",
            "visible_if": "is_business == 'true'",
            "validation_rules": [
              {
                "type": "REQUIRED",
                "error_message": "Company Tax ID is mandatory for corporate accounts"
              }
            ]
          },
          {
            "type": "button",
            "id": "submit_kyc_btn",
            "title": "Submit Verification",
            "variant": "FILLED",
            "is_full_width": true,
            "actions": [
              {
                "type": "submit_form",
                "endpoint": "/api/v1/kyc/verify",
                "method": "POST"
              },
              {
                "type": "show_dialog",
                "title": "Verification Submitted",
                "message": "Your KYC data was validated fail-closed and sent securely.",
                "confirm_text": "Great!"
              }
            ]
          }
        ]
      }
    }
    """.trimIndent()

    val FOOD_DELIVERY_SCREEN = """
    {
      "id": "food_delivery",
      "version": "1.0.0",
      "title": "Food & Delivery Feed",
      "applySafeInsets": true,
      "root": {
        "type": "lazy_column",
        "id": "delivery_col",
        "spacing": 16,
        "padding": 16,
        "items": [
          {
            "type": "card",
            "id": "order_tracker",
            "backgroundColor": "#161D2F",
            "cornerRadius": 16,
            "padding": 16,
            "child": {
              "type": "container",
              "id": "track_box",
              "direction": "VERTICAL",
              "spacing": 10,
              "children": [
                {
                  "type": "container",
                  "id": "track_header",
                  "direction": "HORIZONTAL",
                  "alignment": "CENTER",
                  "spacing": 8,
                  "children": [
                    { "type": "badge", "id": "badge_status", "text": "ON THE WAY 🛵", "backgroundColor": "#00E5FF", "textColor": "#0B0F19" },
                    { "type": "text", "id": "eta", "text": "ETA: 18 mins", "style": "BODY_SMALL", "color": "#94A3B8" }
                  ]
                },
                {
                  "type": "text",
                  "id": "order_desc",
                  "text": "Heimdall Bistro · Order #4892",
                  "style": "TITLE_MEDIUM",
                  "color": "#FFFFFF"
                },
                {
                  "type": "divider",
                  "id": "track_div",
                  "thickness": 1,
                  "color": "#334155"
                },
                {
                  "type": "button",
                  "id": "track_btn",
                  "title": "Live Map Tracking",
                  "variant": "OUTLINED",
                  "is_full_width": true,
                  "actions": [
                    { "type": "show_snackbar", "message": "📍 Driver is 1.2 km away on 5th Avenue." }
                  ]
                }
              ]
            }
          },
          {
            "type": "text",
            "id": "nearby_header",
            "text": "Recommended Kitchens",
            "style": "TITLE_MEDIUM",
            "color": "#FFFFFF"
          },
          {
            "type": "card",
            "id": "restaurant_1",
            "backgroundColor": "#1E293B",
            "cornerRadius": 12,
            "padding": 14,
            "child": {
              "type": "container",
              "id": "rest_row",
              "direction": "HORIZONTAL",
              "spacing": 12,
              "alignment": "CENTER",
              "children": [
                { "type": "icon", "id": "food_ic_1", "name": "restaurant", "tint": "#A855F7", "size": 32 },
                {
                  "type": "container",
                  "id": "rest_info",
                  "direction": "VERTICAL",
                  "spacing": 2,
                  "children": [
                    { "type": "text", "id": "r_name_1", "text": "Artisan Pizza Lab", "style": "BODY_LARGE", "color": "#FFFFFF" },
                    { "type": "text", "id": "r_meta_1", "text": "⭐ 4.9 · Italian · Free Delivery", "style": "BODY_SMALL", "color": "#00E5FF" }
                  ]
                }
              ]
            }
          }
        ]
      }
    }
    """.trimIndent()

    val PAYWALL_SCREEN = """
    {
      "id": "paywall_plans",
      "version": "1.0.0",
      "title": "Subscription Plans",
      "applySafeInsets": true,
      "root": {
        "type": "lazy_column",
        "id": "paywall_list",
        "spacing": 16,
        "padding": 20,
        "items": [
          {
            "type": "container",
            "id": "pw_header",
            "direction": "VERTICAL",
            "spacing": 8,
            "alignment": "CENTER",
            "children": [
              { "type": "badge", "id": "pro_badge", "text": "HEIMUI PRO ACCESS", "backgroundColor": "#A855F7", "textColor": "#FFFFFF" },
              { "type": "text", "id": "pw_title", "text": "Unlock Server Power", "style": "HEADLINE_MEDIUM", "color": "#FFFFFF" },
              { "type": "text", "id": "pw_sub", "text": "Real-time layout streaming and instant OTA UI delivery.", "style": "BODY_MEDIUM", "color": "#94A3B8" }
            ]
          },
          {
            "type": "switch",
            "id": "annual_toggle",
            "state_key": "is_annual",
            "label": "Annual Billing (Save 30% + 2 Months Free)",
            "initial_checked": true
          },
          {
            "type": "card",
            "id": "pricing_card",
            "backgroundColor": "#161D2F",
            "borderColor": "#00E5FF",
            "cornerRadius": 16,
            "padding": 20,
            "child": {
              "type": "container",
              "id": "pricing_content",
              "direction": "VERTICAL",
              "spacing": 12,
              "children": [
                { "type": "text", "id": "price_title", "text": "Enterprise Developer Tier", "style": "TITLE_MEDIUM", "color": "#00E5FF" },
                { "type": "text", "id": "price_val_annual", "text": "$19.00 / month (Billed Yearly)", "style": "TITLE_LARGE", "color": "#FFFFFF", "visible_if": "is_annual == 'true'" },
                { "type": "text", "id": "price_val_monthly", "text": "$29.00 / month", "style": "TITLE_LARGE", "color": "#FFFFFF", "visible_if": "is_annual != 'true'" },
                { "type": "divider", "id": "div_pw", "thickness": 1, "color": "#334155" },
                { "type": "text", "id": "feat_1", "text": "✔ Unlimited Server-Driven UI Screens", "style": "BODY_MEDIUM", "color": "#E2E8F0" },
                { "type": "text", "id": "feat_2", "text": "✔ Cryptographic Payload Verification", "style": "BODY_MEDIUM", "color": "#E2E8F0" },
                { "type": "text", "id": "feat_3", "text": "✔ Automated Failover & Emergency Bundles", "style": "BODY_MEDIUM", "color": "#E2E8F0" }
              ]
            }
          },
          {
            "type": "button",
            "id": "subscribe_cta",
            "title": "Start 14-Day Free Trial",
            "variant": "FILLED",
            "is_full_width": true,
            "actions": [
              {
                "type": "show_dialog",
                "title": "Welcome to HeimUI Pro! 🚀",
                "message": "Your free trial is now active. Enjoy seamless Server-Driven UI updates.",
                "confirm_text": "Explore Studio"
              }
            ]
          }
        ]
      }
    }
    """.trimIndent()

    val PLAYGROUND_SCREEN = """
    {
      "id": "components_playground",
      "version": "1.0.0",
      "title": "Component Catalog",
      "applySafeInsets": true,
      "root": {
        "type": "lazy_column",
        "id": "catalog_col",
        "spacing": 16,
        "padding": 16,
        "items": [
          {
            "type": "card",
            "id": "play_intro",
            "backgroundColor": "#161D2F",
            "cornerRadius": 12,
            "padding": 16,
            "child": {
              "type": "container",
              "id": "intro_c",
              "direction": "VERTICAL",
              "spacing": 6,
              "children": [
                { "type": "badge", "id": "story_b", "text": "STORYBOOK CATALOG", "backgroundColor": "#00E5FF", "textColor": "#0B0F19" },
                { "type": "text", "id": "c_title", "text": "HeimUI Native Primitives", "style": "TITLE_MEDIUM", "color": "#FFFFFF" },
                { "type": "text", "id": "c_sub", "text": "Every component here is rendered natively in Compose Multiplatform from JSON declarations.", "style": "BODY_SMALL", "color": "#94A3B8" }
              ]
            }
          },
          { "type": "text", "id": "hdr_btns", "text": "Button Variants", "style": "TITLE_SMALL", "color": "#00E5FF" },
          {
            "type": "container",
            "id": "btn_box",
            "direction": "VERTICAL",
            "spacing": 8,
            "children": [
              { "type": "button", "id": "btn_filled", "title": "Filled Variant", "variant": "FILLED", "is_full_width": true },
              { "type": "button", "id": "btn_outlined", "title": "Outlined Variant", "variant": "OUTLINED", "is_full_width": true },
              { "type": "button", "id": "btn_tonal", "title": "Tonal Variant", "variant": "TONAL", "is_full_width": true }
            ]
          },
          { "type": "text", "id": "hdr_inputs", "text": "Form Inputs & State", "style": "TITLE_SMALL", "color": "#00E5FF" },
          {
            "type": "text_field",
            "id": "sample_tf",
            "state_key": "sample_text",
            "label": "Dynamic Text Field",
            "placeholder": "Type something to observe state..."
          },
          {
            "type": "custom",
            "id": "sample_custom_chart",
            "name": "stock_chart",
            "data": {
              "ticker": "HEIM",
              "price": 348.50,
              "change": "+14.2%"
            }
          }
        ]
      }
    }
    """.trimIndent()

    fun getScreenJson(screenId: String): String? {
        return when (screenId) {
            "ecommerce_home" -> ECOMMERCE_SCREEN
            "fintech_kyc" -> FINTECH_KYC_SCREEN
            "food_delivery" -> FOOD_DELIVERY_SCREEN
            "paywall_plans" -> PAYWALL_SCREEN
            "components_playground" -> PLAYGROUND_SCREEN
            else -> ECOMMERCE_SCREEN
        }
    }
}
