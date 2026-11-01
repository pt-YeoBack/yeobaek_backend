--------------------------------------------------
-- 1. ENUM 타입 정의
--------------------------------------------------
create type status as enum ('ACTIVE', 'SUSPENDED', 'WITHDRAWN'); -- 회원 상태 (활성, 정지, 탈퇴)
create type member_role as enum ('ADMIN', 'MEMBER'); -- 회원 권한 (관리자, 일반 회원)
create type member_type as enum ('YEOBEAK', 'NAVER', 'KAKAO', 'GOOGLE'); -- 가입 경로 타입
create type rel_status as enum ('PENDING', 'DONE'); -- 친구 관계 상태 (대기 중, 완료)
create type companion_relation as enum ('PENDING', 'DONE', 'REJECT'); -- 동행 관계 상태 (대기 중, 완료, 거절)
create type together_purpose as enum ('MEAL', 'SPOT'); -- 모임 목적 (식사, 장소/관광)
create type together_area as enum ('NEAR', 'OTHER'); -- 모임 지역 구분 (근처, 기타)
create type together_status as enum ('OPEN', 'CLOSE', 'ENDED'); -- 모임 상태 (모집 중, 모집 마감, 종료)
create type together_role as enum ('HOST', 'MEMBER'); -- 모임 내 역할 (방장, 일반 참여자)
create type report_status as enum ('PENDING', 'DONE', 'REJECT'); -- 신고 처리 상태 (대기 중, 처리 완료, 반려)
create type destination_type as enum ('CITY', 'COUNTRY'); -- 목적지 타입 (도시, 국가)
create type trip_category as enum ('DOMESTIC', 'OVERSEAS'); -- 여행 카테고리 (국내, 해외)
create type trip_status as enum ('WRITE', 'SAVE', 'FINISH'); -- 여행 상태 (작성 중, 임시 저장, 완료)


--------------------------------------------------
-- 2. 독립 참조 테이블 (부모 테이블이 없는 기본 테이블)
--------------------------------------------------

-- 회원 테이블
create table tbl_member (
                            id bigint generated always as identity primary key,
                            member_email varchar(255) unique,
                            member_phone varchar(15) unique,
                            member_password varchar(255),
                            member_name varchar(10),
                            member_alias varchar(20),
                            member_bio varchar(255),
                            member_status status default 'ACTIVE',      -- (활동: ACTIVE, 휴면: SUSPENDED, 탈퇴: WITHDRAWN)
                            created_datetime timestamp default now(),
                            updated_datetime timestamp default now()
);

-- 장소
create table tbl_place (
                           id bigint generated always as identity primary key,
                           google_place_id varchar(255) unique,
                           place_name varchar(255) not null,
                           place_address varchar(255) not null,
                           place_lat decimal not null,
                           place_lng decimal not null,
                           created_datetime timestamp default now(),
                           updated_datetime timestamp default now()
);

-- 이미지 파일
create table tbl_file (
                          id bigint generated always as identity primary key,
                          original_name varchar(255) not null,
                          file_name varchar(255) not null,
                          file_path varchar(255) not null,
                          file_size bigint,
                          created_datetime timestamp default now()
);

-- 여행 목적지 (자기참조 구조)
create table tbl_destination (
                                 id bigint generated always as identity primary key,
                                 destination_parent_id bigint,
                                 destination_name varchar(255) not null,
                                 destination_type destination_type not null,
                                 created_datetime timestamp default now(),
                                 constraint fk_destination_parent foreign key (destination_parent_id)
                                     references tbl_destination(id)
);

-- 여행 취향
create table tbl_preference (
                                id bigint generated always as identity primary key,
                                preference_name varchar(30) not null
);

-- 선호 활동
create table tbl_vibe (
                          id bigint generated always as identity primary key,
                          vide_name varchar(30) not null
);


--------------------------------------------------
-- 3. 회원 연관 테이블 (tbl_member 참조)
--------------------------------------------------

-- 회원 권한 및 정보
create table tbl_auth (
                          id bigint not null primary key,
                          member_role member_role default 'MEMBER',     -- (ADMIN, MEMBER)
                          member_type member_type not null,
                          created_datetime timestamp default now(),
                          updated_datetime timestamp default now(),
                          constraint fk_auth_member foreign key (id)
                              references tbl_member(id)
);

-- 회원 개인 설정
create table tbl_member_setting (
                                    id bigint not null primary key,
                                    preference_id bigint not null,
                                    vibe_id bigint not null,
                                    thema_color varchar(30) default '',
                                    alias_public boolean default 'false',
                                    phone_public boolean default 'false',
                                    note_auto_save boolean default 'false',
                                    created_datetime timestamp default now(),
                                    updated_datetime timestamp default now(),
                                    constraint fk_setting_member foreign key (id)
                                        references tbl_member(id) on delete cascade,
                                    constraint fk_setting_preference foreign key (preference_id)
                                        references tbl_preference(id),
                                    constraint fk_setting_vibe foreign key (vibe_id)
                                        references tbl_vibe(id)
);

-- 친구 신청 목록
create table tbl_member_rel (
                                id bigint generated always as identity primary key,
                                rel_requester_id bigint not null,
                                rel_receiver_id bigint not null,
                                rel_status rel_status not null default 'PENDING',
                                created_datetime timestamp default now(),
                                constraint fk_rel_requester foreign key (rel_requester_id)
                                    references tbl_member(id),
                                constraint fk_rel_receiver foreign key (rel_receiver_id)
                                    references tbl_member(id)
);


--------------------------------------------------
-- 4. 모임 및 커뮤니티 테이블
--------------------------------------------------

-- 모임 게시글 (tbl_place 참조)
create table tbl_get_together (
                                  id bigint generated always as identity primary key,
                                  place_id bigint not null,
                                  together_title varchar(255) not null,
                                  together_content text not null,
                                  together_purpose together_purpose default 'SPOT',     -- (장소/관광: SPOT, 식사: MEAL)
                                  together_area together_area default 'NEAR',           -- (근처: NEAR, 기타: OTHER)
                                  together_max_member int not null,
                                  together_start_date timestamp not null,
                                  together_end_date timestamp not null,
                                  together_status together_status default 'OPEN',
                                  created_datetime timestamp default now(),
                                  updated_datetime timestamp default now(),
                                  constraint fk_together_place foreign key (place_id)
                                      references tbl_place(id)
);

-- 모임 참여자 목록 (tbl_get_together, tbl_member 참조)
create table tbl_together_member (
                                     id bigint generated always as identity primary key,
                                     together_id bigint not null,
                                     member_id bigint not null,
                                     together_role together_role not null,
                                     together_guest_member int default 0,
                                     created_datetime timestamp default now(),
                                     constraint fk_together_id foreign key (together_id)
                                         references tbl_get_together(id),
                                     constraint fk_together_member foreign key (member_id)
                                         references tbl_member(id)
);

-- 채팅방 (tbl_get_together 참조)
create table tbl_chat (
                          id bigint generated always as identity primary key,
                          together_id bigint not null,
                          created_datetime timestamp default now(),
                          updated_datetime timestamp default now(),
                          constraint fk_chat_together foreign key (together_id)
                              references tbl_get_together(id)
);

-- 채팅방 인원 목록 (tbl_chat, tbl_member 참조)
create table tbl_chat_member (
                                 id bigint generated always as identity primary key,
                                 chat_id bigint not null,
                                 member_id bigint not null,
                                 joined_at timestamp default now(),
                                 last_read_message_id bigint null,
                                 status varchar(20) default 'JOINED',  -- (참여: JOINED, 나감: LEFT)
                                 created_datetime timestamp default now(),
                                 constraint fk_chat_member_chat foreign key (chat_id)
                                     references tbl_chat(id),
                                 constraint fk_chat_member_member foreign key (member_id)
                                     references tbl_member(id)
);

-- 메시지 (tbl_chat, tbl_member 참조)
create table tbl_chat_message (
                                  id bigint generated always as identity primary key,
                                  chat_id bigint not null,
                                  sender_id bigint not null,
                                  message text not null,
                                  message_type varchar(20) default 'TEXT',   -- 메시지 타입 (TEXT, IMAGE, NOTICE)
                                  created_datetime timestamp default now(),
                                  constraint fk_chat_message_chat foreign key (chat_id)
                                      references tbl_chat(id),
                                  constraint fk_chat_message_sender foreign key (sender_id)
                                      references tbl_member(id)
);


--------------------------------------------------
-- 5. 여행 및 일정 관련 테이블
--------------------------------------------------

-- 여행 (tbl_member, tbl_destination 참조)
create table tbl_trip (
                          id bigint generated always as identity primary key,
                          member_id bigint not null,
                          destination_id bigint not null,
                          departure_id bigint not null,
                          trip_category trip_category not null,
                          trip_title varchar(255) not null,
                          trip_start_date timestamp not null,
                          trip_end_date timestamp not null,
                          trip_status trip_status default 'WRITE',
                          created_datetime timestamp default now(),
                          updated_datetime timestamp default now(),
                          constraint fk_trip_member foreign key (member_id)
                              references tbl_member(id),
                          constraint fk_trip_destination foreign key (destination_id)
                              references tbl_destination(id),
                          constraint fk_trip_departure foreign key (departure_id)
                              references tbl_destination(id)
);

-- 여행 날짜 (tbl_trip 참조)
create table tbl_trip_day (
                              id bigint generated always as identity primary key,
                              trip_id bigint not null,
                              trip_day timestamp not null,
                              trip_day_title varchar(255),
                              created_datetime timestamp default now(),
                              updated_datetime timestamp default now(),
                              constraint fk_day_trip foreign key (trip_id)
                                  references tbl_trip(id)
);

-- 여행 세부일정 (tbl_trip_day, tbl_member, tbl_place 참조)
create table tbl_schedule (
                              id bigint generated always as identity primary key,
                              trip_day_id bigint not null,
                              member_id bigint not null,
                              place_id bigint,
                              schedule_title varchar(255) not null,
                              schedule_start_time timestamp not null,
                              schedule_memo varchar(255),
                              schedule_ai_source boolean default 'false',
                              created_datetime timestamp default now(),
                              updated_datetime timestamp default now(),
                              constraint fk_schedule_trip_day foreign key (trip_day_id)
                                  references tbl_trip_day(id),
                              constraint fk_schedule_member foreign key (member_id)
                                  references tbl_member(id),
                              constraint fk_schedule_place foreign key (place_id)
                                  references tbl_place(id)
);

-- 수첩 하루 기록 (tbl_trip_day, tbl_member 참조)
create table tbl_trip_note (
                               id bigint generated always as identity primary key,
                               trip_day_id bigint not null,
                               member_id bigint not null,
                               note_title varchar(255) not null,
                               note_sentence varchar(255),
                               created_datetime timestamp default now(),
                               updated_datetime timestamp default now(),
                               constraint fk_note_trip_day foreign key (trip_day_id)
                                   references tbl_trip_day(id),
                               constraint fk_note_member foreign key (member_id)
                                   references tbl_member(id)
);

-- 동행 인원 정보 (tbl_trip, tbl_member 참조)
create table tbl_trip_companion (
                                    id bigint generated always as identity primary key,
                                    trip_id bigint not null,
                                    member_id bigint,
                                    companion_is_member boolean default 'false',
                                    companion_name varchar(20),
                                    companion_contact varchar(20),
                                    companion_relation companion_relation default 'REJECT',
                                    created_datetime timestamp default now(),
                                    constraint fk_companion_trip foreign key (trip_id)
                                        references tbl_trip(id),
                                    constraint fk_companion_member foreign key (member_id)
                                        references tbl_member(id)
);


--------------------------------------------------
-- 6. 시스템 관리 및 기타 테이블
--------------------------------------------------

-- 신고 (tbl_member 참조)
create table tbl_report (
                            id bigint generated always as identity primary key,
                            reporter_id bigint not null,
                            target_id bigint not null,
                            report_reason varchar(255) not null,
                            report_content text,
                            report_status report_status default 'PENDING',
                            created_datetime timestamp default now(),
                            updated_datetime timestamp default now(),
                            constraint fk_report_reporter foreign key (reporter_id)
                                references tbl_member(id),
                            constraint fk_report_target foreign key (target_id)
                                references tbl_member(id)
);


--------------------------------------------------
-- 7. 이미지 조인 테이블 (tbl_file 참조)
--------------------------------------------------

-- 유저 프로필 이미지 (tbl_file, tbl_member 참조)
create table tbl_file_member (
                                 id bigint not null primary key,
                                 member_id bigint not null,
                                 constraint fk_file_member_file foreign key (id)
                                     references tbl_file(id),
                                 constraint fk_file_member foreign key (member_id)
                                     references tbl_member(id)
);

-- 여행 이미지 (tbl_file, tbl_trip 참조)
create table tbl_file_trip (
                               id bigint not null primary key,
                               trip_id bigint not null,
                               constraint fk_file_trip_file foreign key (id)
                                   references tbl_file(id),
                               constraint fk_file_trip foreign key (trip_id)
                                   references tbl_trip(id)
);

-- 모임 사진 (tbl_file, tbl_get_together 참조)
create table tbl_file_together (
                                   id bigint not null primary key,
                                   together_id bigint not null,
                                   constraint fk_file_together_file foreign key (id)
                                       references tbl_file(id),
                                   constraint fk_file_together foreign key (together_id)
                                       references tbl_get_together(id)
);

-- 수첩 사진 (tbl_file, tbl_trip_note 참조)
create table tbl_file_note (
                               id bigint not null primary key,
                               note_id bigint not null,
                               constraint fk_file_note_file foreign key (id)
                                   references tbl_file(id),
                               constraint fk_file_note foreign key (note_id)
                                   references tbl_trip_note(id)
);