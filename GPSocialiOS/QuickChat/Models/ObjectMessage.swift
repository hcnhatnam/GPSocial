//  MIT License

//  Copyright (c) 2019 Haik Aslanyan

//  Permission is hereby granted, free of charge, to any person obtaining a copy
//  of this software and associated documentation files (the "Software"), to deal
//  in the Software without restriction, including without limitation the rights
//  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//  copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions:

//  The above copyright notice and this permission notice shall be included in all
//  copies or substantial portions of the Software.

//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
//  SOFTWARE.

import UIKit

class ObjectMessage: FireStorageCodable {
  
  var id = UUID().uuidString
  var message: String?
  var content: String?
  var contentType = ContentType.none
  var timestamp = Int(Date().timeIntervalSince1970)
  var ownerID: String?
  var profilePicLink: String?
  var profilePic: UIImage?

  func encode(to encoder: Encoder) throws {
    var container = encoder.container(keyedBy: CodingKeys.self)
    try container.encode(id, forKey: .id)
    try container.encodeIfPresent(message, forKey: .content)
    try container.encodeIfPresent(timestamp, forKey: .timestamp)
    try container.encodeIfPresent(ownerID, forKey: .sender_id)
    try container.encodeIfPresent(profilePicLink, forKey: .profilePicLink)
    try container.encodeIfPresent(contentType.rawValue, forKey: .contentType)
    try container.encodeIfPresent(content, forKey: .msgContent)
  }
  
  init() {}
  
  public required convenience init(from decoder: Decoder) throws {
    self.init()
    let container = try decoder.container(keyedBy: CodingKeys.self)
    id = try container.decode(String.self, forKey: .id)
    message = try container.decodeIfPresent(String.self, forKey: .content)
    timestamp = try container.decodeIfPresent(Int.self, forKey: .timestamp) ?? Int(Date().timeIntervalSince1970)
    ownerID = try container.decodeIfPresent(String.self, forKey: .sender_id)
    profilePicLink = try container.decodeIfPresent(String.self, forKey: .profilePicLink)
    content = try container.decodeIfPresent(String.self, forKey: .msgContent)
    if let contentTypeValue = try container.decodeIfPresent(Int.self, forKey: .contentType) {
      contentType = ContentType(rawValue: contentTypeValue) ?? ContentType.unknown
    }
  }
}

extension ObjectMessage {
  private enum CodingKeys: String, CodingKey {
    case id
    case content      /// đổi từ message -> content
    case timestamp
    case sender_id
    case profilePicLink
    case contentType
    case msgContent /// tên cũ là content, do web dùng "content"  để lưu "text" nên đổi "message" -> "content", "content" -> "msgContent". Phức tạp vl.
  }
  
  enum ContentType: Int {
    case none
    case photo
    case location
    case unknown
  }
}
