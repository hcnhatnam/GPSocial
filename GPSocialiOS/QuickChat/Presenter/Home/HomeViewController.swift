//
//  HomeViewController.swift
//  QuickChat
//
//  Created by Do Le Duy on 9/19/20.
//  Copyright © 2020 Haik Aslanyan. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class HomeViewController: UIViewController {
  
  let chatWithStrangersButton = UIButton()
  let chatWithFriendButton = UIButton() 
  let personalInfoButton = UIButton()
  let iconImageView = UIImageView()
  
  private let userManager = UserManager()
  private var currentUser: ObjectUser?
  private let userManagerNamServer = LDUserManager()
  
  // MARK: Life cycle
  
  override func viewDidLoad() {
    super.viewDidLoad()
    view.backgroundColor = .white
          
    setupNavigationController()
    setupViews()
    fetchProfile()
  }
  
  func setupViews() {
    
    /// icon
    let iconPaddingTop = 200
    let iconWidth = 100
    view.addSubview(iconImageView)
    iconImageView.image = UIImage(named: homeIconName)
    iconImageView.snp.makeConstraints { (make) in
      make.top.equalTo(iconPaddingTop)
      make.centerX.equalToSuperview()
      make.width.height.equalTo(iconWidth)
    }
    
    /// Go to chat with friends
    let buttonWidth = 200
    let buttonHeight = 50
    let padding = 30
    let cornerRadius: CGFloat = 20
    
    personalInfoButton.setTitle("YOUR IP'S INFO", for: .normal)
    personalInfoButton.backgroundColor = Color.Zalo.blue1
    personalInfoButton.layer.cornerRadius = cornerRadius
    personalInfoButton.clipsToBounds = true
    personalInfoButton.makeShadow()
    view.addSubview(personalInfoButton)
    personalInfoButton.addTarget(self, action: #selector(didTapPersonalInfoButton), for: .touchUpInside)
    personalInfoButton.snp.makeConstraints { (make) in
      make.width.equalTo(buttonWidth)
      make.height.equalTo(buttonHeight)
      make.centerX.equalToSuperview()
      make.top.equalTo(iconImageView.snp.bottom).offset(padding)
    }
    
    
    chatWithFriendButton.setTitle("CHAT WITH FRIENDS", for: .normal)
    chatWithFriendButton.backgroundColor = Color.Zalo.blue1
    chatWithFriendButton.layer.cornerRadius = cornerRadius
    chatWithFriendButton.clipsToBounds = true
    chatWithFriendButton.makeShadow()
    view.addSubview(chatWithFriendButton)
    chatWithFriendButton.addTarget(self, action: #selector(didTapChatWithFriendButton), for: .touchUpInside)
    chatWithFriendButton.snp.makeConstraints { (make) in
      make.width.equalTo(buttonWidth)
      make.height.equalTo(buttonHeight)
      make.centerX.equalToSuperview()
      make.top.equalTo(personalInfoButton.snp.bottom).offset(padding)
    }
    
    /// Chat with strangers
    chatWithStrangersButton.setTitle("FIND NEW FRIENDS", for: .normal)
    chatWithStrangersButton.backgroundColor = Color.Zalo.blue1
    chatWithStrangersButton.layer.cornerRadius = cornerRadius
    chatWithStrangersButton.clipsToBounds = true
    chatWithStrangersButton.makeShadow()
    chatWithStrangersButton.addTarget(self, action: #selector(didTapChatWithStrangersButton), for: .touchUpInside)
    view.addSubview(chatWithStrangersButton)
    chatWithStrangersButton.snp.makeConstraints { (make) in
      make.width.equalTo(buttonWidth)
      make.height.equalTo(buttonHeight)
      make.centerX.equalToSuperview()
      make.top.equalTo(chatWithFriendButton.snp.bottom).offset(padding)
    }
    
    
    
  }
  
  override func viewWillAppear(_ animated: Bool) {
    super.viewWillAppear(animated)
    showNavigationBar(enableShow: false)
  }
  
  private func setupNavigationController() {
    /// Disable swipe to back
    navigationController?.interactivePopGestureRecognizer?.isEnabled = false
    
    /// Hide naviagtionbar
    navigationController?.navigationBar.isHidden = true
  }
  
  func fetchProfile() {
    userManager.currentUserData {[weak self] user in
      guard let user = user else {return}
      
      self?.currentUser = user
      self?.loginToNamServer(user: user)
    }
  }
  
  func loginToNamServer(user: ObjectUser) {
    userManagerNamServer.login(user: user) { (messageError) in
      if let messageError = messageError {
        self.showAlert(title: "Alert", message: messageError, completion: nil)
      }
    }
  }
  
  // MARK: Events handler
  
  @objc private func didTapChatWithStrangersButton() {
    let vc = MapViewController()
    vc.modalPresentationStyle = .fullScreen
    show(vc, sender: self)
  }
  
  @objc private func didTapChatWithFriendButton() {
    let vc = UIStoryboard.initial(storyboard: .conversations)
    let appDelegate = UIApplication.shared.delegate as! AppDelegate
    appDelegate.window?.rootViewController = navigationController
    //vc.modalPresentationStyle = .fullScreen
    show(vc, sender: self)
  }
  
  @objc private func didTapPersonalInfoButton() {
    let vc = IPInforViewController()
    vc.modalPresentationStyle = .pageSheet
    show(vc, sender: self)
  }
}
