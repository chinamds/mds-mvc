package com.mds.aiotplayer.cm.util;

import com.mds.aiotplayer.cm.rest.ResourceRest;
import com.mds.aiotplayer.i18n.util.I18nUtils;

/// <summary>
/// Contains functionality for returning resources in a form that can be consumed on the client.
/// </summary>
public final class ResourceUtils
{
	/// <summary>
	/// Gets an object containing String resources. The object can be JSON parsed and sent to the client.
	/// </summary>
	/// <returns>An instance of <see cref="Resource" />.</returns>
	public static ResourceRest getResourceEntity(){
		ResourceRest resourceRest = new ResourceRest();
		resourceRest.HdrSearchButtonTt = I18nUtils.getMessage("search.Button_Tooltip");
		resourceRest.HdrUserAlbumLinkTt = I18nUtils.getMessage("header.Go_To_Home_Album_Link_Tooltip");
		resourceRest.HdrMyAccountTt = I18nUtils.getMessage("login.My_Account_Link_Text");
		resourceRest.HdrLoginLinkText = I18nUtils.getMessage("login.Button_Text");
		resourceRest.HdrLogoutTt = I18nUtils.getMessage("login.Logout_Text");
		resourceRest.HdrCreateAccountLinkText = I18nUtils.getMessage("login.Create_Account_Text");
		resourceRest.LpRecent = I18nUtils.getMessage("uc.leftPane.RecentlyAdded_Text");
		resourceRest.LpTopRated = I18nUtils.getMessage("uc.leftPane.TopRated_Text");
		resourceRest.LpTags = I18nUtils.getMessage("uc.leftPane.Tags_Text");
		resourceRest.LpPeople = I18nUtils.getMessage("uc.leftPane.People_Text");
		resourceRest.LpSelectPlayer = I18nUtils.getMessage("uc.leftPane.SelectPlayer_Text");
		resourceRest.AbmPgrFirstTt = I18nUtils.getMessage("uc.thumbnailView.Pager_First_Tooltip");
		resourceRest.AbmPgrLastTt = I18nUtils.getMessage("uc.thumbnailView.Pager_Last_Tooltip");
		resourceRest.AbmPfx = I18nUtils.getMessage("site.Album_Lbl");
		resourceRest.AbmIsPvtTt = I18nUtils.getMessage("uc.album.Is_Private_Tt");
		resourceRest.AbmNotPvtTt = I18nUtils.getMessage("uc.album.Not_Private_Tt");
		resourceRest.AbmAnonDisabledTt = I18nUtils.getMessage("uc.album.Anon_Disabled_Tt");
		resourceRest.AbmAnonDisabledTitle = I18nUtils.getMessage("uc.album.Anon_Disabled_Title");
		resourceRest.AbmAnonDisabledMsg = I18nUtils.getMessage("uc.album.Anon_Disabled_Msg");
		resourceRest.AbmPvtChngd = I18nUtils.getMessage("uc.album.Visibility_Changed_Tt");
		resourceRest.AbmOwnrTt = I18nUtils.getMessage("uc.album.Assign_Owner_Tt");
		resourceRest.AbmOwnr = I18nUtils.getMessage("uc.album.Owner_Tt");
		resourceRest.AbmOwnrDtl = I18nUtils.getMessage("uc.album.Owner_Dtl");
		resourceRest.AbmOwnrLbl = I18nUtils.getMessage("uc.album.Owner_Lbl");
		resourceRest.AbmOwnrInhtd = I18nUtils.getMessage("uc.album.Inherited_Owners_Lbl");
		resourceRest.AbmOwnrChngd = I18nUtils.getMessage("uc.album.Owner_Changed_Hdr");
		resourceRest.AbmOwnrClrd = I18nUtils.getMessage("uc.album.Owner_Removed");
		resourceRest.AbmOwnrChngdDtl = I18nUtils.getMessage("uc.album.Owner_Changed_Dtl");
		resourceRest.AbmOwnrTtDtl = I18nUtils.getMessage("uc.album.Owner_Tt_Dtl");
		resourceRest.AbmRssTt = I18nUtils.getMessage("uc.album.Rss_Tt");
		resourceRest.AbmPgrNextTt = I18nUtils.getMessage("uc.thumbnailView.Pager_Next_Tooltip");
		resourceRest.AbmPgrPrevTt = I18nUtils.getMessage("uc.thumbnailView.Pager_Previous_Tooltip");
		resourceRest.AbmPgrStatus = I18nUtils.getMessage("uc.thumbnailView.Pager_Status");
		resourceRest.AbmNumObjSuffix = I18nUtils.getMessage("uc.album.Num_Obj_Suffix");
		resourceRest.AbmShareAlbum = I18nUtils.getMessage("uc.album.Share_Hdr");
		resourceRest.AbmLinkToAlbum = I18nUtils.getMessage("uc.album.Link_Hdr");
		resourceRest.AbmDwnldZip = I18nUtils.getMessage("uc.thumbnailView.Album_Download_Zip_Tooltip");
		resourceRest.AbmRvsSortTt = I18nUtils.getMessage("uc.album.Reverse_Sort_Tt");
		resourceRest.AbmSortbyTt = I18nUtils.getMessage("uc.album.Sort_By_Tt");
		resourceRest.AbmSortbyCustom = I18nUtils.getMessage("uc.album.Sort_By_Custom");
		resourceRest.AbmSortbyTitle = I18nUtils.getMessage("uc.album.Sort_By_Title");
		resourceRest.AbmSortbyRating= I18nUtils.getMessage("uc.album.Sort_By_Rating");
		resourceRest.AbmSortbyDatePictureTaken = I18nUtils.getMessage("uc.album.Sort_By_DatePictureTaken");
		resourceRest.AbmSortbyDateAdded = I18nUtils.getMessage("uc.album.Sort_By_DateAdded");
		resourceRest.AbmSortbyFilename = I18nUtils.getMessage("uc.album.Sort_By_Filename");
		resourceRest.AbmNoObj = I18nUtils.getMessage("uc.thumbnailView.Intro_Text_No_Objects");
		resourceRest.AbmAddObj = I18nUtils.getMessage("uc.thumbnailView.Intro_Text_Add_Objects");

		resourceRest.MoPrev = I18nUtils.getMessage("uc.moView.Prev_Tt");
		resourceRest.MoNext = I18nUtils.getMessage("uc.moView.Next_Tt");
		resourceRest.MoTbEmbed = I18nUtils.getMessage("uc.moView.Tb_Download_Tt");
		resourceRest.MoTbSsStart = I18nUtils.getMessage("uc.moView.Tb_Ss_Start_Tt");
		resourceRest.MoTbSsStop = I18nUtils.getMessage("uc.moView.Tb_Ss_Pause_Tt");
		resourceRest.MoTbMove = I18nUtils.getMessage("uc.moView.Tb_Move_Tt");
		resourceRest.MoTbCopy = I18nUtils.getMessage("uc.moView.Tb_Copy_Tt");
		resourceRest.MoTbRotate = I18nUtils.getMessage("uc.moView.Tb_Rotate_Tt");
		resourceRest.MoTbDelete = I18nUtils.getMessage("uc.moView.Tb_Delete_Tt");
		resourceRest.MoTbApprove = I18nUtils.getMessage("uc.moView.Tb_Approve_Tt");
		resourceRest.MoTbReject = I18nUtils.getMessage("uc.moView.Tb_Reject_Tt");
		resourceRest.MoPosSptr = I18nUtils.getMessage("uc.contentObjectView.Position_Separator_Text");
		resourceRest.MoShare = I18nUtils.getMessage("uc.moView.Tb_Share_Tt");
		resourceRest.MoShareThisPage = I18nUtils.getMessage("uc.moView.Tb_Share_Url_Lbl");
		resourceRest.MoShareHtml = I18nUtils.getMessage("uc.moView.Tb_Share_Embed_Lbl");
		resourceRest.MoShareDwnld = I18nUtils.getMessage("uc.moView.Tb_Share_Download_Lbl");
		resourceRest.MoShareSlctThmb = I18nUtils.getMessage("uc.moView.Tb_Share_Thmb_Lbl");
		resourceRest.MoShareSlctOpt = I18nUtils.getMessage("uc.moView.Tb_Share_Opt_Lbl");
		resourceRest.MoShareSlctOrg = I18nUtils.getMessage("uc.moView.Tb_Share_Org_Lbl");
		resourceRest.MoShareDwnldFile = I18nUtils.getMessage("uc.moView.Tb_Share_Download_Link");
		resourceRest.MoShareDwnldZip = I18nUtils.getMessage("uc.moView.Tb_Share_DownloadAlbum_Link");
		resourceRest.MoShareDwnldZipTt = I18nUtils.getMessage("uc.moView.Tb_Share_DownloadAlbum_Link_Tt");
		resourceRest.MoNoSsHdr = I18nUtils.getMessage("uc.moView.NoSs_Hdr");
		resourceRest.MoNoSsBdy = I18nUtils.getMessage("uc.moView.NoSs_Bdy");

		resourceRest.ContentCaptionEditSave = I18nUtils.getMessage("uc.moView.Save");
		resourceRest.ContentCaptionEditCancel = I18nUtils.getMessage("uc.moView.Cancel");
		resourceRest.ContentCaptionEditSaving = I18nUtils.getMessage("uc.moView.Saving");
		resourceRest.ContentCaptionEditTt = I18nUtils.getMessage("uc.moView.Edit_Tt");
		resourceRest.ContentDeleteConfirm = I18nUtils.getMessage("uc.moView.Tb_Delete_Confirm");
		resourceRest.ContentApprovalLbl = I18nUtils.getMessage("uc.moView.Tb_Approval_Lbl");
		resourceRest.ContentApprovalStatusLbl = I18nUtils.getMessage("uc.moView.Tb_ApprovalStatus_Lbl");
		resourceRest.ContentApprovalDateLbl = I18nUtils.getMessage("uc.moView.Tb_ApprovalDate_Lbl");
		resourceRest.MetaEditPlaceholder = I18nUtils.getMessage("uc.moView.Meta_Edit_Placeholder");
		resourceRest.SyncStarting = I18nUtils.getMessage("task.synch.Progress_SynchStarting");
		resourceRest.SyncAborting = I18nUtils.getMessage("task.synch.Progress_SynchCanceling");
		resourceRest.SyncAbort = I18nUtils.getMessage("task.synch.Cancel_Button_Text");

		resourceRest.ContentApproved  = I18nUtils.getMessage("uc.moView.Approval_Approved");
		resourceRest.ContentRejected  = I18nUtils.getMessage("uc.moView.Approval_Rejected");
		resourceRest.ContentNoAction  = I18nUtils.getMessage("uc.moView.Approval_NoAction");
		resourceRest.ContentUnapproved  = I18nUtils.getMessage("uc.moView.Approval_Unapproved");
		resourceRest.ContentUnrejected  = I18nUtils.getMessage("uc.moView.Approval_Unrejected");
		
		return resourceRest;
	}
}